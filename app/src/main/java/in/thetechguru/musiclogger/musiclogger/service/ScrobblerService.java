package in.thetechguru.musiclogger.musiclogger.service;

/**
 * Created by amit on 19/2/18.
 */

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.List;

import in.thetechguru.musiclogger.musiclogger.logger.Logger;
import in.thetechguru.musiclogger.musiclogger.model.MediaSessionMetaData;

@RequiresApi(21)
public class ScrobblerService extends Service {

    private Binder mBinder;
    private MediaController.Callback controllerCallback;

    //for maintaining currently playing media and sending it to db once media changes
    private MediaSessionMetaData currentMedia;

    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null)
            mBinder = new Binder();
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MediaSessionManager.OnActiveSessionsChangedListener listener = new MediaSessionManager.OnActiveSessionsChangedListener() {
            @Override
            public void onActiveSessionsChanged(final List<MediaController> controllers) {
                if (controllers.size() > 0) {
                    final MediaController controller = controllers.get(0);
                    Log.d("ScrobblerService", "onActiveSessionsChanged: " + controller.getPackageName());
                    if (controllerCallback != null)
                        controller.unregisterCallback(controllerCallback);
                    controllerCallback = new MediaController.Callback() {
                        @Override
                        public void onMetadataChanged(@Nullable MediaMetadata metadata) {
                            super.onMetadataChanged(metadata);

                            if (metadata != null) {
                                Log.d("ScrobblerService", "Artist: " + metadata.getString(MediaMetadata.METADATA_KEY_ARTIST));
                                Log.d("ScrobblerService", "Title: " + metadata.getString(MediaMetadata.METADATA_KEY_TITLE));
                                Log.d("ScrobblerService", "Album: " + metadata.getString(MediaMetadata.METADATA_KEY_ALBUM));
                                Log.d("ScrobblerService", "Genre: " + metadata.getString(MediaMetadata.METADATA_KEY_GENRE));
                                Log.d("ScrobblerService", "Album Artist: " + metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST));

                                String data = controller.getPackageName() + "\n"
                                        + "Title : " + metadata.getString(MediaMetadata.METADATA_KEY_TITLE) + "\n"
                                        + "Artist : " + metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)  + "\n"
                                        + "Album : "+ metadata.getString(MediaMetadata.METADATA_KEY_ALBUM)   + "\n"
                                        + "Genre : "+ metadata.getString(MediaMetadata.METADATA_KEY_GENRE)   + "\n"
                                        + "Alt artist : " + metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST) + "\n"
                                        + "Duration : " + metadata.getLong(MediaMetadata.METADATA_KEY_DURATION) + "\n"
                                        + "Album Art : " + metadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI) + "\n"
                                        + "Author : " + metadata.getString(MediaMetadata.METADATA_KEY_AUTHOR) + "\n"
                                        + "Composer : " + metadata.getString(MediaMetadata.METADATA_KEY_COMPOSER) + "\n"
                                        + "Date : " + metadata.getString(MediaMetadata.METADATA_KEY_DATE) + "\n"
                                        + "Description : " + metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION) + "\n"
                                        + "Title : " + metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE) + "\n"
                                        + "Subtitle : " + metadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE) + "\n"
                                        + "Track Number : " + metadata.getLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER) + "\n"
                                        + "Track Rating : " + metadata.getRating(MediaMetadata.METADATA_KEY_RATING) + "\n"
                                        + "Time Played At : "+ System.currentTimeMillis()  + "\n\n";

                                Logger.logMusicData( data
                                         );

                            }
                        }

                        @Override
                        public void onAudioInfoChanged(MediaController.PlaybackInfo info) {
                            super.onAudioInfoChanged(info);
                            Log.d("ScrobblerService", "onAudioInfoChanged: ");

                        }

                        @Override
                        public void onExtrasChanged(@Nullable Bundle extras) {
                            super.onExtrasChanged(extras);
                            Log.d("ScrobblerService", "onExtrasChanged: ");
                            if(extras!=null) {
                                StringBuilder logString = new StringBuilder();
                                for (String key : extras.keySet()) {
                                    logString.append(key).append(" : ").append(extras.get(key)).append("\n");
                                }
                                Logger.logMusicData(logString+"\n\n");
                            }
                        }

                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onPlaybackStateChanged(@NonNull PlaybackState state) {
                            super.onPlaybackStateChanged(state);
                            Log.d("ScrobblerService", "onPlaybackStateChanged: playing " + (state.getState() == PlaybackState.STATE_PLAYING));
                        }
                    };

                    controller.registerCallback(controllerCallback);

                }
            }
        };
        MediaSessionManager manager = ((MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE));
        if (manager != null) {
            manager.addOnActiveSessionsChangedListener(listener, new ComponentName(getApplicationContext(), NotificationListener.class));
        }

    }
}