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

@Entity(tableName = "artists", indices = {@Index(value = {"artist_name"}, unique = true)})
public class Artist {

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        public long id;

        @ColumnInfo(name = "artist_name")
        public String artist_name;

        public Artist(){}

    @Ignore
        public Artist(@NonNull String artist_name){
                this.artist_name = artist_name;
        }
}
