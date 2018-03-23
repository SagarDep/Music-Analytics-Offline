
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

package in.thetechguru.musiclogger.musiclogger.service;

import android.service.notification.NotificationListenerService;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;
import android.util.Log;


@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.KITKAT)
public class NotificationListener extends android.service.notification.NotificationListenerService {

    private IBinder mBinder;

    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null)
            mBinder = super.onBind(intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            onListenerConnected();
        return mBinder;
    }

    @Override
    @SuppressWarnings("NewApi")
    public void onCreate() {
        super.onCreate();
        if (!isListeningAuthorized(this))
            return;
        startScrobblingService();
        Log.d("NotificationListener", "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mBinder = null;
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
    }

    @Override
    @TargetApi(24)
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        requestRebind(new ComponentName(getApplicationContext(), NotificationListenerService.class));
    }

    private void startScrobblingService() {
        Log.d("NotificationListener", "startScrobblingService: starting scrobbling service");
        Intent intent = new Intent(getApplicationContext(), ScrobblerService.class);
        startService(intent);
    }

    /**
     * check if permission is there toEpoch read notifications
     * @param context If you are android developer and you don't know context, may god bless you
     * @return boolean interval_status about permission
     */
    public static boolean isListeningAuthorized(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = context.getPackageName();

        return !(enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName));
    }

    /**
     * Check if background service for reading media sessions and pushing data toEpoch DB is running
     * @param context context
     * @return interval_status
     */
    public static boolean isAppScrobbling(Context context) {
        Log.d("NotificationListener", "isAppScrobbling: " + ScrobblerService.isServiceRunning);
        return ScrobblerService.isServiceRunning;
    }

    /* NLS Stuff */

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.d("NotificationListener", "onNotificationPosted: " + sbn.getPackageName());
        //Logger.logMusicData("Notification posted \n");
        if (!isAppScrobbling(this))
            startScrobblingService();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) // Hide that "
            snoozeSystemNotification(sbn);
    }

    @RequiresApi(26)
    private void snoozeSystemNotification(StatusBarNotification sbn) {

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d("NotificationListener", "onNotificationRemoved: " + sbn.getPackageName());
    }
}