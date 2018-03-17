package `in`.thetechguru.musiclogger.musiclogger.ui

/**
 * Created by amit on 19/2/18.
 * *  * This file is part of Music Logger
 *  * Copyright © 2017 Music Logger
 *  *
 *  * Music Logger is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * Music Logger is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  * You should have received a copy of the GNU General Public License
 *  * along with Music Logger.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

import `in`.thetechguru.musiclogger.musiclogger.service.NotificationListener
import `in`.thetechguru.musiclogger.musiclogger.R
import `in`.thetechguru.musiclogger.musiclogger.data.DataModel
import `in`.thetechguru.musiclogger.musiclogger.data.db.MusicRecordsDB
import `in`.thetechguru.musiclogger.musiclogger.data.model_classes.Interval
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import java.util.concurrent.Executors
import com.github.mikephil.charting.components.Legend
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*


class ActivityMain : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var dataModel: DataModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        if(!NotificationListener.isListeningAuthorized(this)){
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
            Toast.makeText(this, "Click on Music Logger to enable!", Toast.LENGTH_LONG).show()
        }

        Executors.newSingleThreadExecutor().execute({
            val songs = MusicRecordsDB.getInstance(applicationContext)?.MusicRecordDAO()?.getAllSongs()
            val artists = MusicRecordsDB.getInstance(applicationContext)?.MusicRecordDAO()?.getAllArtists()
            val albums = MusicRecordsDB.getInstance(applicationContext)?.MusicRecordDAO()?.getAllAlbums()
            val genres = MusicRecordsDB.getInstance(applicationContext)?.MusicRecordDAO()?.getAllGenres()
            val records = MusicRecordsDB.getInstance(applicationContext)?.MusicRecordDAO()?.getAll()

            val stat_string = "Total records : ${records?.size} \n Total artists : ${artists?.size} " +
                    "\n Total albums : ${albums?.size} " +
                    "\n Total songs : ${songs?.size} " +
                    "\n Total genre : ${genres?.size} \n"

            var artist_string = ""
            artists?.forEach {
                artist -> artist_string = artist_string + "${artist.artist_name} : ${artist.id} \n"
            }

            var songs_string = ""
            songs?.forEach {
                song -> songs_string = songs_string + "${song.song_name} : ${song.id} \n"
            }


            var albums_string = ""
            albums?.forEach {
                album -> albums_string = albums_string + "${album.album_name} : ${album.id} \n"
            }

            var records_string = ""
            records?.forEach {
                record -> records_string = records_string + "$record \n"
            }

            dataModel = DataModel()
            dataModel!!.init()
            val data = dataModel!!.getArtistSongCount(Interval(Interval.LIFETIME))

            Handler(Looper.getMainLooper()).post{

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

                chart.data = data
                chart.invalidate()
                stats.text = stat_string
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        when (id) {
            R.id.action_settings -> {
                    val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                    startActivity(intent)
                }
        }


        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        displaySelectedScreen(item.itemId)
        return true
    }

    private fun displaySelectedScreen(itemId: Int) {

        //creating fragment object
        var fragment: Fragment? = null

        //initializing the fragment object which is selected
        when (itemId) {
            R.id.nav_music -> fragment = FragmentMusic()
            R.id.nav_youtube -> fragment = FragmentYoutube()
            R.id.nav_history -> fragment = FragmentHistory()
            R.id.nav_artists -> fragment = FragmentArtists()
            R.id.nav_about -> fragment = FragmentAbout()
            R.id.nav_faq -> fragment = FragmentFaq()
        }

        //replacing the fragment
        if (fragment != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.container, fragment)
            ft.commit()
        }

        drawer_layout.closeDrawer(GravityCompat.START)
    }
}
