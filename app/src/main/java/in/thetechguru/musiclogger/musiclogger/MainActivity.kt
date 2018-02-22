package `in`.thetechguru.musiclogger.musiclogger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.content.Intent



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(!NotificationListener.isListeningAuthorized(this)){
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
            Toast.makeText(this, "Click on Music Logger to enable!", Toast.LENGTH_LONG).show()
        }
    }
}
