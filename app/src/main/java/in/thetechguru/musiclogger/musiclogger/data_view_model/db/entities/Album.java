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


@Entity(tableName = "albums", indices = {@Index(value = {"album_name"}, unique = true)})
public class Album {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "album_name")
    public String album_name = "";

    public Album(){}

    @Ignore
    public Album(@NonNull String album_name){
        this.album_name = album_name;
    }
}
