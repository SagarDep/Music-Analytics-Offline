package in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import in.thetechguru.musiclogger.musiclogger.MyApp;
import in.thetechguru.musiclogger.musiclogger.data_view_model.model_classes.MediaSessionMetaData;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Created by abami on 22-Feb-18.
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
    @PrimaryKey(autoGenerate = true) public int id;
    @ColumnInfo(name = "artist_id", index = true) public Long artist_id;
    @ColumnInfo(name = "album_id", index = true) public Long album_id;
    @ColumnInfo(name = "genre_id", index = true) public Long genre_id;
    @ColumnInfo(name = "song_id", index = true) public Long song_id;
    @ColumnInfo(name = "played_at") public Long played_at;
    @ColumnInfo(name = "approx_played_for") public Long approx_played_for;
    @ColumnInfo(name = "total_duration") public Long total_duration;
    @ColumnInfo(name = "package_name") public String package_name;

    public MusicRecord(){}

    public MusicRecord(MediaSessionMetaData metaData){
        played_at = metaData.getStarted_playing_at();
        total_duration = metaData.getTotal_duration();
        approx_played_for = metaData.getApprox_played_for();
        package_name = metaData.getPackage_name();
    }

    public MusicRecord(Long artist_id, Long album_id, Long genre_id, Long song_id){
        this.artist_id = artist_id;
        this.album_id = album_id;
        this.genre_id = genre_id;
        this.song_id = song_id;
    }

    @Override
    public String toString() {
        return " Id : " + id + " : Artist id : " + artist_id + " : Album id : " + album_id + " : Song id :" + song_id + " : Duration :" + total_duration ;
    }
}


