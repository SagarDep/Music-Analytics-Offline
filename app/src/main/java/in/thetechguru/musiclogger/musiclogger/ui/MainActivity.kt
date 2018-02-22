package `in`.thetechguru.musiclogger.musiclogger.ui

import `in`.thetechguru.musiclogger.musiclogger.service.NotificationListener
import `in`.thetechguru.musiclogger.musiclogger.R
import `in`.thetechguru.musiclogger.musiclogger.db.MusicRecordsDB
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import android.util.Log
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
}
