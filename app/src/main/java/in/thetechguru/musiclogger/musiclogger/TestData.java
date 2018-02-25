package in.thetechguru.musiclogger.musiclogger;

import android.util.Log;

import java.util.UUID;

import in.thetechguru.musiclogger.musiclogger.data_view_model.db.MusicRecordsDB;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.MusicRecordsDao;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.Album;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.Artist;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.Genre;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.MusicRecord;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.Song;
import in.thetechguru.musiclogger.musiclogger.data_view_model.model_classes.MediaSessionMetaData;

/**
 *    Created by amit on 24/2/18.
 *  * This file is part of Music Logger
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

public class TestData {

    public static void insertData(){

        MusicRecordsDao dbDao = MusicRecordsDB.getInstance(MyApp.getInstance()).MusicRecordDAO();

        for(int i=0; i<100; i++){
            String song_ = i + UUID.randomUUID().toString();
            String artist_ = "AB";
            String album_ = i + UUID.randomUUID().toString();
            String genre_ = i + UUID.randomUUID().toString();
            String package_name_ = i + UUID.randomUUID().toString();

            Song song = new Song(song_);

            Artist artist = new Artist(artist_);
            dbDao.insert(artist);

            Album album = new Album(album_);

            Genre genre = new Genre(genre_);

            MusicRecord musicRecord = new MusicRecord(dbDao.getArtistId(artist_),dbDao.insert(album), dbDao.insert(genre),  dbDao.insert(song));


            Log.d("DataModel", "pushRecord: " + musicRecord);

            //insert actual record
            dbDao.insert(musicRecord);

        }
    }

}
