package `in`.thetechguru.musiclogger.musiclogger.view

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

import `in`.thetechguru.musiclogger.musiclogger.MyApp
import `in`.thetechguru.musiclogger.musiclogger.service.NotificationListener
import `in`.thetechguru.musiclogger.musiclogger.R
import `in`.thetechguru.musiclogger.musiclogger.tasks.GetCSV
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class ActivityMain : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.setCheckedItem(R.id.nav_music)
        displaySelectedScreen(R.id.nav_music)

        if(!NotificationListener.isListeningAuthorized(this)){
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
            Toast.makeText(this, "Click on Music Logger toEpoch enable!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items toEpoch the action bar if it is present.
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
            R.id.action_export -> {
                if(!GetCSV.isTaskRunning) GetCSV().execute()
            }
            R.id.action_toggle_toast -> {
                val pref = PreferenceManager.getDefaultSharedPreferences(MyApp.getInstance())
                if(pref.getBoolean(MyApp.getInstance().getString(R.string.pref_toast), true)){
                    pref.edit().putBoolean(MyApp.getInstance().getString(R.string.pref_toast), false).apply()
                    Toast.makeText(MyApp.getInstance(), "Turned off", Toast.LENGTH_SHORT).show()
                }else{
                    pref.edit().putBoolean(MyApp.getInstance().getString(R.string.pref_toast), true).apply()
                    Toast.makeText(MyApp.getInstance(), "Turned on", Toast.LENGTH_SHORT).show()
                }
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
            R.id.nav_faq -> {
                fragment = FragmentFaq()
                return
            }
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
