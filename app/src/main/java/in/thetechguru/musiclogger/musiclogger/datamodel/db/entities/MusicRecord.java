package in.thetechguru.musiclogger.musiclogger.datamodel.db.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo.MediaSessionMetaData;

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
    @ColumnInfo(name = "started_playing_at") public Long started_playing_at;
    @ColumnInfo(name = "approx_played_for") public Long approx_played_for;
    @ColumnInfo(name = "total_duration") public Long total_duration;
    @ColumnInfo(name = "package_name") public String package_name;

    public MusicRecord(){}

    @Ignore
    public MusicRecord(MediaSessionMetaData metaData){
        started_playing_at = metaData.getStarted_playing_at();
        total_duration = metaData.getTotal_duration();
        approx_played_for = metaData.getApprox_played_for();
        package_name = metaData.getPackage_name();
    }

    @Override
    public String toString() {
        return "Record Id : " + id
                + "\nPackage Name : " + package_name
                + "\nArtistLastFm id : " + artist_id
                + "\nAlbum id : " + album_id
                + "\nSong id : " + song_id
                + "\nTotal Duration : " + total_duration
                + "\nStarted playing at : " + started_playing_at
                + "\nPlayed For : " + approx_played_for
                + "\n\n";
    }
}


