package in.thetechguru.musiclogger.musiclogger.service;

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
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import in.thetechguru.musiclogger.musiclogger.data_view_model.DataModel;
import in.thetechguru.musiclogger.musiclogger.data_view_model.model_classes.MediaSessionMetaData;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

@RequiresApi(21)
public class ScrobblerService extends Service {

    private Binder mBinder;
    private MediaController.Callback controllerCallback;
    public static boolean isServiceRunning = false;

    //for maintaining currently playing media and sending it to db once media changes
    private MediaSessionMetaData currentMediaMetaData;
    private Disposable disposable;
    private DataModel dataModel;


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

    /**
     * create flowable and start receiving changing meta data from media session callbacks
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ScrobblerService", "onCreate: service started");
        MediaSessionManager manager = ((MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE));
        if (manager != null) {
            disposable = observeMetadata(manager)
                    .sample(3, TimeUnit.SECONDS)
                    .observeOn(Schedulers.io())
                    .subscribeWith(new DisposableSubscriber<MediaSessionMetaData>() {
                        @Override
                        public void onNext(final MediaSessionMetaData mediaSessionMetaData) {
                            Log.d("ScrobblerService", "onNext: " + mediaSessionMetaData);
                            if(currentMediaMetaData == null){
                                currentMediaMetaData = mediaSessionMetaData;
                            }else {
                                pushRecord(mediaSessionMetaData);
                            }
                        }

                        @Override
                        public void onError(Throwable t) {
                            Log.d("ScrobblerService", "onError: " + t.getLocalizedMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.d("ScrobblerService", "onComplete: ");
                        }
                });
        }
        dataModel = new DataModel();
        dataModel.init();
        isServiceRunning = true;
    }

    /**
     * push record on DB
     * @param mediaSessionMetaData all the data we need to make entry in DB
     */
    private void pushRecord(MediaSessionMetaData mediaSessionMetaData){
        currentMediaMetaData.setApproxPlayTime();

        Log.d("ScrobblerService", "pushRecord: " + currentMediaMetaData);
        dataModel.pushRecord(mediaSessionMetaData);

        //update current media with latest one
        currentMediaMetaData = mediaSessionMetaData;
    }

    /**
     * dispose all reactive things we use here
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(disposable!=null && !disposable.isDisposed()) disposable.dispose();
        Toast.makeText(this, "Scrobbler turning off", Toast.LENGTH_SHORT).show();
        Log.d("ScrobblerService", "onDestroy: service destroyed");
        isServiceRunning = false;
    }

    /**
     * returns observable which keep emitting media session changes until eternity
     * @param manager Media session manager instance
     * @return Observable
     */
    public Flowable<MediaSessionMetaData> observeMetadata(final MediaSessionManager manager){
        return Flowable.create(new FlowableOnSubscribe<MediaSessionMetaData>() {
            @Override
            public void subscribe(final FlowableEmitter<MediaSessionMetaData> emitter) throws Exception {
                final MediaSessionManager.OnActiveSessionsChangedListener listener = new MediaSessionManager.OnActiveSessionsChangedListener() {
                    @Override
                    public void onActiveSessionsChanged(final List<MediaController> controllers) {
                        if (controllers.size() > 0) {
                            final MediaController controller = controllers.get(0);
                            Log.d("ScrobblerService", "onActiveSessionsChanged: " + controller.getPackageName());
                            if (controllerCallback != null) {
                                for(MediaController mediaController: controllers) {
                                    mediaController.registerCallback(controllerCallback);
                                }
                            }
                            controllerCallback = new MediaController.Callback() {
                                    @Override
                                    public void onMetadataChanged(@Nullable final MediaMetadata metadata) {
                                        super.onMetadataChanged(metadata);
                                        if (metadata != null) {
                                            Log.d("ScrobblerService", "Artist: " + metadata.getString(MediaMetadata.METADATA_KEY_ARTIST));
                                            emitter.onNext(new MediaSessionMetaData(metadata, controller.getPackageName()));
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
                                            Log.d("ScrobblerService", "onExtrasChanged: " + logString);
                                        }
                                    }

                                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                                    @Override
                                    public void onPlaybackStateChanged(@NonNull PlaybackState state) {
                                        super.onPlaybackStateChanged(state);
                                        Log.d("ScrobblerService", "onPlaybackStateChanged: playing " + (state.getState() == PlaybackState.STATE_PLAYING));
                                    }
                                };
                            for(MediaController mediaController: controllers) {
                                mediaController.registerCallback(controllerCallback);
                            }
                        }
                    }
                };

                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        Log.d("ScrobblerService", "cancel: removing listeners");
                        manager.removeOnActiveSessionsChangedListener(listener);
                    }
                });

                manager.addOnActiveSessionsChangedListener(listener, new ComponentName(getApplicationContext(), NotificationListener.class));
            }
        }, BackpressureStrategy.BUFFER);
    }
}