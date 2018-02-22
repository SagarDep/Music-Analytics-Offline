package in.thetechguru.musiclogger.musiclogger.logger;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by amit on 19/2/18.
 */

public class Logger {

    public static void logMusicData(String log){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/music_log.txt");
        try {
             FileWriter fw = new FileWriter(file,true); //the true will append the new data
             fw.write(log);//appends the string to the file
             fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
