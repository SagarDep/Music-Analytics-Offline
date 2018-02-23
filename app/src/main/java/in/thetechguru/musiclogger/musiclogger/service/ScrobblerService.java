package in.thetechguru.musiclogger.musiclogger.service;

/**
 * Created by amit on 19/2/18.
 *
 */

import android.annotation.TargetApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import org.intellij.lang.annotations.Flow;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import in.thetechguru.musiclogger.musiclogger.logger.Logger;
import in.thetechguru.musiclogger.musiclogger.model.MediaSessionMetaData;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.operators.flowable.FlowableFromCallable;
import io.reactivex.subscribers.DisposableSubscriber;

@RequiresApi(21)
public class ScrobblerService extends Service {

    private Binder mBinder;
    private MediaController.Callback controllerCallback;
    public static boolean isServiceRunning = false;

    //for maintaining currently playing media and sending it to db once media changes
    private MediaSessionMetaData currentMedia;

    private Disposable disposable;

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
        MediaSessionManager manager = ((MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE));
        if (manager != null) {
            disposable = observeMetadata(manager)
                    .sample(3, TimeUnit.SECONDS)
                    .subscribeWith(new DisposableSubscriber<MediaSessionMetaData>() {
                        @Override
                        public void onNext(MediaSessionMetaData mediaSessionMetaData) {
                            Log.d("ScrobblerService", "onNext: " + mediaSessionMetaData);
                            if(currentMedia==null){
                                currentMedia = mediaSessionMetaData;
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
        isServiceRunning = true;
    }

    private void pushRecord(MediaSessionMetaData mediaSessionMetaData){
        currentMedia.setApproxPlayTime();
        //logic to push record on db
        Log.d("ScrobblerService", "pushRecord: " + currentMedia);
        //update current media with latest one
        currentMedia = mediaSessionMetaData;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(disposable!=null && !disposable.isDisposed()) disposable.dispose();
        Toast.makeText(this, "Scrobbler turning off", Toast.LENGTH_SHORT).show();
        isServiceRunning = false;
    }

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
                                controller.unregisterCallback(controllerCallback);
                            }
                            controllerCallback = new MediaController.Callback() {
                                    @Override
                                    public void onMetadataChanged(@Nullable final MediaMetadata metadata) {
                                        super.onMetadataChanged(metadata);
                                        if (metadata != null) {
                                            Log.d("ScrobblerService", "Artist: " + metadata.getString(MediaMetadata.METADATA_KEY_ARTIST));
                                            emitter.onNext(new MediaSessionMetaData(metadata));
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
                            controller.registerCallback(controllerCallback);
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