package `in`.thetechguru.musiclogger.musiclogger.ui

import `in`.thetechguru.musiclogger.musiclogger.service.NotificationListener
import `in`.thetechguru.musiclogger.musiclogger.R
import `in`.thetechguru.musiclogger.musiclogger.data_view_model.db.MusicRecordsDB
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!NotificationListener.isListeningAuthorized(this)){
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
            Toast.makeText(this, "Click on Music Logger to enable!", Toast.LENGTH_LONG).show()
        }

        Executors.newSingleThreadExecutor().execute(Runnable {

            val songs = MusicRecordsDB.getInstance(applicationContext)?.MusicRecordDAO()?.getAllSongs()
            val artists = MusicRecordsDB.getInstance(applicationContext)?.MusicRecordDAO()?.getAllArtists()
            val albums = MusicRecordsDB.getInstance(applicationContext)?.MusicRecordDAO()?.getAllAlbums()
            val genres = MusicRecordsDB.getInstance(applicationContext)?.MusicRecordDAO()?.getAllGenres()
            val records = MusicRecordsDB.getInstance(applicationContext)?.MusicRecordDAO()?.getAll()

            val stat_string = "Total records : ${records?.size} \n Total artists : ${artists?.size} " +
                    "\n Total albums : ${albums?.size} " +
                    "\n Total songs : ${songs?.size} " +
                    "\n Total genre : ${genres?.size} \n"

            var artist_string:String = ""
            artists?.forEach {
                artist -> artist_string = artist_string + "${artist.artist_name} \n"
            }

            var records_string:String = ""
            records?.forEach {
                record -> records_string = records_string + "${record.toString()} \n"
            }


            stats.setText(stat_string)
            records_text.setText(records_string)
            artists_text.setText(artist_string)

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
}
