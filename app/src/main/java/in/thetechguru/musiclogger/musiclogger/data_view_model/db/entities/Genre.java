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


@Entity(tableName = "genres", indices = {@Index(value = {"genre_name"}, unique = true)})
public class Genre {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "genre_name")
    public String genre_name;

    public Genre(){}

    @Ignore
    public Genre(@NonNull String genre_name){
        this.genre_name = genre_name;
    }
}
