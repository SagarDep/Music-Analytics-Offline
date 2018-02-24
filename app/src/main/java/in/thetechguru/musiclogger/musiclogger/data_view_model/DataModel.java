package in.thetechguru.musiclogger.musiclogger.data_view_model;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import in.thetechguru.musiclogger.musiclogger.MyApp;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.MusicRecordsDB;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.MusicRecordsDao;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.Album;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.Artist;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.Genre;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.MusicRecord;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.Song;
import in.thetechguru.musiclogger.musiclogger.data_view_model.model_classes.MediaSessionMetaData;

/**
 * Created by abami on 24-Feb-18.
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

/**
 * Data model of whole application
 * This object should not (and hopefully will not) hold reference to any kind of view or UI context.
 * Cause memory leaks ain't to easy to find
 */

public class DataModel extends ViewModel{

    private MusicRecordsDao dbDao;

    @SuppressWarnings("ConstantConditions")
    public void init(){
        if(dbDao==null){
            dbDao = MusicRecordsDB.getInstance(MyApp.getInstance()).MusicRecordDAO();
        }
    }

    /**
     * push music record in table
     * @param mediaSessionMetaData - everything we are going to need to make entry
     *                             in table is enclosed in this model class
     *                             Check MediaSessionMetaData model class for more
     */
    public void pushRecord(@NonNull MediaSessionMetaData mediaSessionMetaData){
        Song song = new Song(mediaSessionMetaData.getTitle());

        String artist_name = mediaSessionMetaData.getArtist();
        if(artist_name.equals(""))
            artist_name = mediaSessionMetaData.getAlt_artist();
        Artist artist = new Artist(artist_name);
        Album album = new Album(mediaSessionMetaData.getAlbum());
        Genre genre = new Genre(mediaSessionMetaData.getGenre());
        MusicRecord musicRecord = new MusicRecord(mediaSessionMetaData);

        //check if record for parent tables exists, if not, insert them
        long artist_id = dbDao.getArtistId(artist_name);
        if(artist_id<=0){
            musicRecord.artist_id = dbDao.insert(artist);
        }else {
            musicRecord.artist_id = artist_id;
        }

        long song_id = dbDao.getSongId(song.song_name);
        if(song_id<=0){
            musicRecord.song_id = dbDao.insert(song);
        }else {
            musicRecord.song_id = song_id;
        }

        long album_id = dbDao.getAlbumId(album.album_name);
        if(album_id<=0){
            musicRecord.album_id = dbDao.insert(album);
        }else {
            musicRecord.album_id = album_id;
        }

        long genre_id = dbDao.getGenreId(genre.genre_name);
        if(genre_id <=0){
            musicRecord.genre_id = dbDao.insert(genre);
        }else {
            musicRecord.genre_id = genre_id;
        }

        Log.d("DataModel", "pushRecord: " + musicRecord);

        //insert actual record
        dbDao.insert(musicRecord);
    }

    private void insertArtist(){

    }

}
