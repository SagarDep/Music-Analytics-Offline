package in.thetechguru.musiclogger.musiclogger;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

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

        Stetho.initializeWithDefaults(this);
    }

    public static MyApp getInstance(){
        return instance;
    }
}
