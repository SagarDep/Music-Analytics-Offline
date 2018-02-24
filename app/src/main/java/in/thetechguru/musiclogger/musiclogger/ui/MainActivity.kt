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
import `in`.thetechguru.musiclogger.musiclogger.TestData
import `in`.thetechguru.musiclogger.musiclogger.data_view_model.db.MusicRecordsDB
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import android.os.Handler
import android.os.Looper
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

        Executors.newSingleThreadExecutor().execute({

            //TestData.insertData()

            //raw data show in text view in main activity

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

            var albums_string = ""
            albums?.forEach {
                album -> albums_string = albums_string + "${album.album_name} : ${album.id} \n"
            }

            var records_string = ""
            records?.forEach {
                record -> records_string = records_string + "${record.toString()} \n"
            }

            Handler(Looper.getMainLooper()).post{
                stats.setText(stat_string)
                records_text.setText(records_string)
                artists_text.setText(artist_string)
                albums_text.setText(albums_string)
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
}
