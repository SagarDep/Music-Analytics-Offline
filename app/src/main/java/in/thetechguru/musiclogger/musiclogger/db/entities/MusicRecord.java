package in.thetechguru.musiclogger.musiclogger.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by abami on 22-Feb-18.
 */

@Entity(tableName = "music_records",
        foreignKeys = {@ForeignKey(entity = Artist.class
                , parentColumns = "id"
                , childColumns = "artist_id", onDelete = CASCADE),

        @ForeignKey(entity = Album.class
                , parentColumns = "id"
                , childColumns = "album_id", onDelete = CASCADE),

        @ForeignKey(entity = Genre.class
                , parentColumns = "id"
                , childColumns = "genre_id", onDelete = CASCADE),

        @ForeignKey(entity = Song.class
                , parentColumns = "id"
                , childColumns = "song_id", onDelete = CASCADE)}
        )
public class MusicRecord {
    @PrimaryKey public int id;
    @ColumnInfo(name = "artist_id") public String artist_id;
    @ColumnInfo(name = "album_id") public String album_id;
    @ColumnInfo(name = "genre_id") public String genre_id;
    @ColumnInfo(name = "song_id") public String song_id;
    @ColumnInfo(name = "played_at") public Long played_at;
    @ColumnInfo(name = "approx_played_for") public String approx_played_for;
    @ColumnInfo(name = "total_duration") public String total_duration;
    @ColumnInfo(name = "package_name") public String package_name;
}
