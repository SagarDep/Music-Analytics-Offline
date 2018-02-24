package in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by abami on 24-Feb-18.
 */

@Entity(tableName = "songs", indices = {@Index(value = {"song_name"}, unique = true)})
public class Song {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "song_name")
    public String song_name;

    public Song(){}

    @Ignore
    public Song(@NonNull String song_name){
        this.song_name = song_name;
    }

}
