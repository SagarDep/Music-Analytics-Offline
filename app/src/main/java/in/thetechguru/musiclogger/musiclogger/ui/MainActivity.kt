package `in`.thetechguru.musiclogger.musiclogger.ui

import `in`.thetechguru.musiclogger.musiclogger.service.NotificationListener
import `in`.thetechguru.musiclogger.musiclogger.R
import `in`.thetechguru.musiclogger.musiclogger.db.MusicRecordsDB
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
            val data = MusicRecordsDB.getInstance(applicationContext)?.MusicRecordDAO()?.getAll()
            Log.d("MainActivity :", data.toString());
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
