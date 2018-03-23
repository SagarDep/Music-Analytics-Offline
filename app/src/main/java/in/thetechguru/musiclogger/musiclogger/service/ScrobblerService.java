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
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.thetechguru.musiclogger.musiclogger.datamodel.Repo;
import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo.MediaSessionMetaData;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;


/**
 * this service should run indefinitely in client mobile
 * no matter what
 */
@RequiresApi(21)
public class ScrobblerService extends Service {

    private Binder mBinder;
    public static boolean isServiceRunning = false;

    //for maintaining currently playing media and sending it toEpoch db once media changes
    private MediaSessionMetaData currentMediaMetaData;
    private Disposable disposable;
    private Repo repo;

    private Handler handler;

    HashMap<MediaController, MediaController.Callback> map;


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
     * create flowable and start receiving changing meta ArtistData fromEpoch media session callbacks
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ScrobblerService", "onCreate: service started");
        MediaSessionManager manager = ((MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE));
        if (manager != null) {
            disposable = observeMetadata(manager)
                    .debounce(2, TimeUnit.SECONDS)  //avoid multiple media changes toEpoch be propagated
                    //.sample(1, TimeUnit.SECONDS)    //sample latest fromEpoch last 10 seconds, useful in case user is skipping songs
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
        map = new HashMap<>();
        repo = Repo.getRepo();
        handler = new Handler(getMainLooper());
        isServiceRunning = true;
    }

    /**
     * push record on DB
     * @param mediaSessionMetaData all the ArtistData we need toEpoch make entry in DB
     */
    private void pushRecord(@NonNull MediaSessionMetaData mediaSessionMetaData){
        if(currentMediaMetaData.isValidRecord()) {
            Log.d("ScrobblerService", "pushRecord: " + currentMediaMetaData);
            repo.pushRecord(currentMediaMetaData);
            final String s = currentMediaMetaData.toString();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ScrobblerService.this, "Pushing " + s, Toast.LENGTH_SHORT).show();
                }
            });
        }
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
        if(currentMediaMetaData!=null && currentMediaMetaData.isValidRecord()) {
            repo.pushRecord(currentMediaMetaData);
        }
        Toast.makeText(this, "Scrobbler turning off", Toast.LENGTH_SHORT).show();
        Log.d("ScrobblerService", "onDestroy: service destroyed");
        isServiceRunning = false;
        map.clear();
        map = null;
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

                            for(MediaController mediaController: controllers) {
                                if(map.get(mediaController)!=null) mediaController.unregisterCallback(map.get(mediaController));
                            }

                            map.clear();

                            for(final MediaController mediaController: controllers) {
                                MediaController.Callback controllerCallback = new MediaController.Callback() {
                                    private String packageName = mediaController.getPackageName() ;

                                    @Override
                                    public void onMetadataChanged(@Nullable final MediaMetadata metadata) {
                                        super.onMetadataChanged(metadata);
                                        if (metadata != null) {
                                            Log.d("onMetadataChanged", "ArtistLastFm: " + metadata.getString(MediaMetadata.METADATA_KEY_ARTIST));

                                            //filters will come here eventually after monitoring metadata changes for various packages
                                            String artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
                                            if(packageName.equals("com.google.android.youtube") && (artist==null || artist.equals(""))) return;

                                            emitter.onNext(new MediaSessionMetaData(metadata, packageName));
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

                                mediaController.registerCallback(controllerCallback);
                                map.put(mediaController, controllerCallback);
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