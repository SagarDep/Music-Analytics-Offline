package in.thetechguru.musiclogger.musiclogger;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by amit on 23/2/18.
 */

public class MyApp extends Application {

    private static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }

    public static MyApp getInstance(){
        return instance;
    }
}
