package in.thetechguru.musiclogger.musiclogger.data_view_model;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import in.thetechguru.musiclogger.musiclogger.MyApp;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.MusicRecordsDB;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.MusicRecordsDao;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.Album;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.Artist;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.Genre;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.MusicRecord;
import in.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.Song;
import in.thetechguru.musiclogger.musiclogger.data_view_model.model_classes.MediaSessionMetaData;
import in.thetechguru.musiclogger.musiclogger.data_view_model.model_classes.ArtistData;

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
    private HashMap<String, Long> artistPlayTime;
    private HashMap<String, Integer> artistSongCount;
    private HashMap<String, ArrayList<String>> artistSongList;
    private HashMap<String, TimeTuple> artistPercentTime;


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


    /**
     * fill the 3 hashmaps to represent artist data
     */
    public void setArtistData(){
        List<ArtistData> artistData =  dbDao.getArtistInfo();

        artistPlayTime = new HashMap<>();
        artistSongCount = new HashMap<>();
        artistSongList = new HashMap<>();
        artistPercentTime = new HashMap<>();

        for(ArtistData artData: artistData){

            //update overall play time for artist
            if(artistPlayTime.containsKey(artData.artist_name)){
                Long recalculatedDuration = artistPlayTime.get(artData.artist_name) + artData.approx_played_for;
                artistPlayTime.put(artData.artist_name, recalculatedDuration);
            }else {
                artistPlayTime.put(artData.artist_name, artData.approx_played_for);
            }

            //update number of song for artist
            if(artistSongList.containsKey(artData.artist_name)){
                artistSongList.get(artData.artist_name).add(artData.song_name);
            }else {
                ArrayList<String> temp = new ArrayList<>();
                temp.add(artData.song_name);
                artistSongList.put(artData.artist_name, temp);
            }


            if(artistPercentTime.containsKey(artData.artist_name)){
                Long newTotalDuration = artistPercentTime.get(artData.artist_name).totalTime + artData.total_duration;
                Long newPlayedFor = artistPercentTime.get(artData.artist_name).playedTime + artData.approx_played_for;
                artistPercentTime.put(artData.artist_name, new TimeTuple(newTotalDuration, newPlayedFor));
            }else {
                artistPercentTime.put(artData.artist_name, new TimeTuple(artData.total_duration, artData.approx_played_for));
            }


        }

        for (Object o : artistSongList.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            System.out.println(pair.getKey() + " = " + pair.getValue());
            artistSongCount.put(pair.getKey() + "", ((ArrayList<Long>) pair.getValue()).size());
        }

        Log.d("DataModel", "setArtistData: ");

    }

    public HashMap<String, Long> getArtistPlayTime() {
        if(artistPlayTime==null){
            setArtistData();
        }
        return artistPlayTime;
    }

    public HashMap<String, ArrayList<String>> getArtistSongList() {
        if(artistSongList==null){
            setArtistData();
        }
        return artistSongList;
    }

    public HashMap<String, Integer> getArtistSongCount() {
        if(artistSongCount==null){
            setArtistData();
        }
        return artistSongCount;
    }

    public HashMap<String, TimeTuple> getArtistPercentTime() {
        if(artistPercentTime==null){
            setArtistData();
        }
        return artistPercentTime;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        artistPlayTime = null;
        artistSongCount = null;
        artistSongList = null;
        artistPercentTime = null;

    }

    class TimeTuple{

        TimeTuple(Long totalTime, Long playedTime){
            this.totalTime = totalTime;
            this.playedTime = playedTime;
        }

        Long totalTime;
        Long playedTime;
    }
}
