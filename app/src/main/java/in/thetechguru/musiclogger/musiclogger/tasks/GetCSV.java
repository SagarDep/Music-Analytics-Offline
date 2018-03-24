package in.thetechguru.musiclogger.musiclogger.tasks;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;

import in.thetechguru.musiclogger.musiclogger.MyApp;
import in.thetechguru.musiclogger.musiclogger.datamodel.Repo;
import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo.CsvRecord;

/**
 * Created by abami on 24-Mar-18.
 */

public class GetCSV extends AsyncTask {

    public static boolean isTaskRunning = false;
    private String filePath;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        isTaskRunning = true;
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/music_records.csv";
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        CSVWriter writer;
        try {
            writer = new CSVWriter(new FileWriter(filePath)
                    , '\t');
            
            //write header
            writer.writeNext(CsvRecord.Companion.getCsvHeaderString().split("#"));
            
            //write entries
            for(CsvRecord record: Repo.getRepo().getCSVRecords()){
                String[] entries = record.toString().split("#");
                writer.writeNext(entries);
            }
            writer.close();
        } catch (IOException e) {
            Toast.makeText(MyApp.getInstance(), "Error writing file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Toast.makeText(MyApp.getInstance(), "File written at " + filePath, Toast.LENGTH_SHORT).show();
        isTaskRunning = false;
    }
}
