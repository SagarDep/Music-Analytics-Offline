package `in`.thetechguru.musiclogger.musiclogger.view

import `in`.thetechguru.musiclogger.musiclogger.R
import `in`.thetechguru.musiclogger.musiclogger.helpers.StatConfig
import `in`.thetechguru.musiclogger.musiclogger.viewmodel.ActivityMainDataModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.github.mikephil.charting.components.Legend
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.disposables.DisposableContainer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_music.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


/**
 * Created by abami on 17-Mar-18.
 */
class FragmentMusic: Fragment(), DatePickerDialog.OnDateSetListener  {

    private var dataModel: ActivityMainDataModel? = null
    private var statConfig: StatConfig = StatConfig()
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataModel = ViewModelProviders.of(activity).get(ActivityMainDataModel::class.java)
        dataModel!!.init()

        compositeDisposable.add(dataModel!!.getArtistInfo(arrayOf("Enrique Iglesias"))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    artistInfo -> Log.d("TAG", artistInfo.corrected_artist)
                })

        return inflater?.inflate(R.layout.fragment_music, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapterMostHeard = ArrayAdapter.createFromResource(context,
                R.array.most_heard, android.R.layout.simple_spinner_item)
        adapterMostHeard.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_most_heard.adapter = adapterMostHeard
        spinner_most_heard.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0 -> {
                        statConfig.element_status = StatConfig.TYPE_ARTIST
                        populateChart(statConfig)
                    }
                    1 -> {
                        statConfig.element_status = StatConfig.TYPE_TRACK
                        populateChart(statConfig)
                    }
                    2 -> {
                        statConfig.element_status = StatConfig.TYPE_ALBUM
                        populateChart(statConfig)
                    }
                    3 -> {
                        statConfig.element_status = StatConfig.TYPE_GENRE
                        populateChart(statConfig)
                    }
                }
            }
        }

        val statType = ArrayAdapter.createFromResource(context,
                R.array.show, android.R.layout.simple_spinner_item)
        statType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_show_stat.adapter = statType
        spinner_show_stat.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0 -> {
                        statConfig.type_status = StatConfig.TYPE_PLAY_TIME
                        populateChart(statConfig)
                    }
                    1 -> {
                        statConfig.type_status = StatConfig.TYPE_PLAY_COUNT
                        populateChart(statConfig)
                    }
                    2 -> {
                        statConfig.type_status = StatConfig.TYPE_PLAY_PERCENTAGE
                        populateChart(statConfig)
                    }
                }
            }
        }

        val adapterInterval = ArrayAdapter.createFromResource(context,
                R.array.for_interval, android.R.layout.simple_spinner_item)
        adapterInterval.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_interval.adapter = adapterInterval
        spinner_interval.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position){
                    0 -> populateChart(statConfig)
                    6 -> {
                        val now = Calendar.getInstance()
                        val dpd = DatePickerDialog.newInstance(
                                this@FragmentMusic,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        )
                        dpd.show(activity.fragmentManager, "Datepickerdialog")
                    }
                    else -> populateChart(statConfig)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        populateChart(statConfig)
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {

        val df = SimpleDateFormat("yyyy-MM-d", Locale.US)
        statConfig.interval_status = StatConfig.CUSTOM

        //from
        val fromDateString = "$year-${monthOfYear+1}-$dayOfMonth"
        val fromDate = df.parse(fromDateString)
        statConfig.fromEpoch = fromDate.time
        statConfig.fromDate = fromDateString

        //to
        val toDateString = "$yearEnd-${monthOfYearEnd+1}-$dayOfMonthEnd"
        val toDate = df.parse(toDateString)
        statConfig.toEpoch = toDate.time
        statConfig.toDate = toDateString

        populateChart(statConfig)
    }

    private fun populateChart(statConfig: StatConfig) {
        Executors.newSingleThreadExecutor().execute({

            val data = dataModel!!.getPieData(statConfig)

            //DISPLAY DATA
            Handler(Looper.getMainLooper()).post {

                chart.description.isEnabled = false;

                chart.centerText = "Amit"
                chart.setCenterTextSize(20f)

                // radius of the center hole in percent of maximum radius
                chart.holeRadius = 45f
                chart.transparentCircleRadius = 50f

                val l = chart.getLegend()
                l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                l.orientation = Legend.LegendOrientation.VERTICAL
                l.setDrawInside(false)

                chart.animateX(1000)
                chart.animateY(1000)
                chart.data = data
                chart.invalidate()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}