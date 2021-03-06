package in.thetechguru.musiclogger.musiclogger.data;

import android.arch.lifecycle.ViewModel;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import in.thetechguru.musiclogger.musiclogger.MyApp;
import in.thetechguru.musiclogger.musiclogger.data.db.MusicRecordsDB;
import in.thetechguru.musiclogger.musiclogger.data.db.MusicRecordsDao;
import in.thetechguru.musiclogger.musiclogger.data.db.entities.Album;
import in.thetechguru.musiclogger.musiclogger.data.db.entities.Artist;
import in.thetechguru.musiclogger.musiclogger.data.db.entities.Genre;
import in.thetechguru.musiclogger.musiclogger.data.db.entities.MusicRecord;
import in.thetechguru.musiclogger.musiclogger.data.db.entities.Song;
import in.thetechguru.musiclogger.musiclogger.data.model_classes.AlbumData;
import in.thetechguru.musiclogger.musiclogger.data.model_classes.Interval;
import in.thetechguru.musiclogger.musiclogger.data.model_classes.MediaSessionMetaData;
import in.thetechguru.musiclogger.musiclogger.data.model_classes.ArtistData;
import in.thetechguru.musiclogger.musiclogger.data.model_classes.SongsData;

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

    private Interval interval;

    //artist related data
    private Map<String, Long> artistPlayTime;
    private Map<String, Integer> artistSongCount;
    private Map<String, HashSet<String>> artistSongList;
    private Map<String, TimeTuple> artistPercentTime;

    //album related data
    private Map<String, Long> albumPlayTime;
    private Map<String, Integer> albumSongCount;
    private Map<String, ArrayList<String>> albumSongList;
    private Map<String, TimeTuple> albumPercentTime;

    //songs related data
    private Map<String, Long> songPlayTime;
    private Map<String, Integer> songSongCount;
    private Map<String, ArrayList<String>> songSongList;
    private Map<String, TimeTuple> songPercentTime;

    @SuppressWarnings("ConstantConditions")
    public void init(){
        if(dbDao==null){
            dbDao = MusicRecordsDB.getInstance(MyApp.getInstance()).MusicRecordDAO();
        }
        interval = new Interval(Interval.LIFETIME);
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
    private void setArtistData(){
        List<ArtistData> artistData =  dbDao.getArtistInfo();

        artistPlayTime = new HashMap<>();
        artistSongCount = new HashMap<>();
        artistSongList = new HashMap<>();
        artistPercentTime = new HashMap<>();

        for(ArtistData artData: artistData){

            //split artist independently
            String[] artists;
            if(artData.artist_name.contains(",")) {
                artists = artData.artist_name.split(",");
            } else if(artData.artist_name.contains("&")){
                artists = artData.artist_name.split("&");
            } else {
                artists = new String[]{artData.artist_name};
            }

            for(String artist:artists){
                artist = artist.trim();
                //update overall play time for artist
                if(artistPlayTime.containsKey(artist)){
                    Long recalculatedDuration = artistPlayTime.get(artist) + artData.approx_played_for;
                    artistPlayTime.put(artist, recalculatedDuration);
                }else {
                    artistPlayTime.put(artist, artData.approx_played_for);
                }

                //update number of song for artist
                if(artistSongList.containsKey(artist)){
                    artistSongList.get(artist).add(artData.song_name);
                }else {
                    HashSet<String> temp = new HashSet<>();
                    temp.add(artData.song_name);
                    artistSongList.put(artist, temp);
                }

                if(artistPercentTime.containsKey(artist)){
                    Long newTotalDuration = artistPercentTime.get(artist).totalTime + artData.total_duration;
                    Long newPlayedFor = artistPercentTime.get(artist).playedTime + artData.approx_played_for;
                    artistPercentTime.put(artist, new TimeTuple(newTotalDuration, newPlayedFor));
                }else {
                    artistPercentTime.put(artist, new TimeTuple(artData.total_duration, artData.approx_played_for));
                }
            }
        }

        for (Object o : artistSongList.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            System.out.println(pair.getKey() + " = " + pair.getValue());
            artistSongCount.put(pair.getKey() + "", ((HashSet<String>) pair.getValue()).size());
        }

        //sort datasets
        artistPlayTime = sortByLong(artistPlayTime, false);
        artistSongCount = sortByInteger(artistSongCount, false);

        Log.d("DataModel", "setArtistData: ");
    }

    /**
     * fill the 3 hashmaps to represent artist data
     */
    private void setAlbumData(){
        List<AlbumData> albumData =  dbDao.getAlbumInfo();

        albumPlayTime = new HashMap<>();
        albumSongCount = new HashMap<>();
        albumSongList = new HashMap<>();
        albumPercentTime = new HashMap<>();

        for(AlbumData albData: albumData){

            //update overall play time for album
            if(albumPlayTime.containsKey(albData.album_name)){
                Long recalculatedDuration = albumPlayTime.get(albData.album_name) + albData.approx_played_for;
                albumPlayTime.put(albData.album_name, recalculatedDuration);
            }else {
                albumPlayTime.put(albData.album_name, albData.approx_played_for);
            }

            //update number of song for album
            if(albumSongList.containsKey(albData.album_name)){
                albumSongList.get(albData.album_name).add(albData.song_name);
            }else {
                ArrayList<String> temp = new ArrayList<>();
                temp.add(albData.song_name);
                albumSongList.put(albData.album_name, temp);
            }


            if(albumPercentTime.containsKey(albData.album_name)){
                Long newTotalDuration = albumPercentTime.get(albData.album_name).totalTime + albData.total_duration;
                Long newPlayedFor = albumPercentTime.get(albData.album_name).playedTime + albData.approx_played_for;
                albumPercentTime.put(albData.album_name, new TimeTuple(newTotalDuration, newPlayedFor));
            }else {
                albumPercentTime.put(albData.album_name, new TimeTuple(albData.total_duration, albData.approx_played_for));
            }


        }

        for (Object o : albumSongList.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            System.out.println(pair.getKey() + " = " + pair.getValue());
            albumSongCount.put(pair.getKey() + "", ((ArrayList<Long>) pair.getValue()).size());
        }

        Log.d("DataModel", "setalbumData: ");

    }

    /**
     * fill the 3 hashmaps to represent artist data
     */
    private void setSongsData(){
        List<SongsData> songsData =  dbDao.getSongsInfo();

        songPlayTime = new HashMap<>();
        songSongCount = new HashMap<>();
        songSongList = new HashMap<>();
        songPercentTime = new HashMap<>();

        for(SongsData songData: songsData){

            //update overall play time for song
            if(songPlayTime.containsKey(songData.song_name)){
                Long recalculatedDuration = songPlayTime.get(songData.song_name) + songData.approx_played_for;
                songPlayTime.put(songData.song_name, recalculatedDuration);
            }else {
                songPlayTime.put(songData.song_name, songData.approx_played_for);
            }

            //update number of song for song
            if(songSongList.containsKey(songData.song_name)){
                songSongList.get(songData.song_name).add(songData.song_name);
            }else {
                ArrayList<String> temp = new ArrayList<>();
                temp.add(songData.song_name);
                songSongList.put(songData.song_name, temp);
            }


            if(songPercentTime.containsKey(songData.song_name)){
                Long newTotalDuration = songPercentTime.get(songData.song_name).totalTime + songData.total_duration;
                Long newPlayedFor = songPercentTime.get(songData.song_name).playedTime + songData.approx_played_for;
                songPercentTime.put(songData.song_name, new TimeTuple(newTotalDuration, newPlayedFor));
            }else {
                songPercentTime.put(songData.song_name, new TimeTuple(songData.total_duration, songData.approx_played_for));
            }


        }

        for (Object o : songSongList.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            System.out.println(pair.getKey() + " = " + pair.getValue());
            songSongCount.put(pair.getKey() + "", ((ArrayList<Long>) pair.getValue()).size());
        }

        Log.d("DataModel", "setsongData: ");

    }

    public PieData getArtistPlayTime(Interval interval) {
        if(artistPlayTime==null || !this.interval.equals(interval)){
            this.interval = interval;
            setArtistData();
        }

        ArrayList<PieEntry> entries1 = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Long> entry : artistPlayTime.entrySet())
        {
            if(index>=6) break;
            String key = entry.getKey();
            Long value = entry.getValue();
            entries1.add(new PieEntry(value, key));
            index++;
        }
        PieDataSet dataSet = new PieDataSet(entries1, "Results");

        //@todo do some styling for data to be displayd on pie chart
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        return new PieData(dataSet);
    }

    public PieData getArtistSongCount(Interval interval) {
        if(artistSongCount==null || !this.interval.equals(interval)){
            this.interval = interval;
            setArtistData();
        }

        ArrayList<PieEntry> entries1 = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Integer> entry : artistSongCount.entrySet())
        {
            if(index>=6) break;
            String key = entry.getKey();
            Integer value = entry.getValue();
            entries1.add(new PieEntry(value, key));
            index++;
        }
        PieDataSet dataSet = new PieDataSet(entries1, "Results");

        //@todo do some styling for data to be displayd on pie chart
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        return new PieData(dataSet);
    }

    public Map<String, HashSet<String>> getArtistSongList(Interval interval) {
        if(artistSongList==null && !this.interval.equals(interval)){
            this.interval = interval;
            setArtistData();
        }
        return artistSongList;
    }

    public Map<String, TimeTuple> getArtistPercentTime(Interval interval) {
        if(artistPercentTime==null && !this.interval.equals(interval)){
            this.interval = interval;
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

    private Map<String, Long> sortByLong(Map<String, Long> unsortMap, final boolean order)
    {

        List<Map.Entry<String, Long>> list = new LinkedList<Map.Entry<String, Long>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Long>>()
        {
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Long> sortedMap = new LinkedHashMap<String, Long>();
        for (Map.Entry<String, Long> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    private Map<String, Integer> sortByInteger(Map<String, Integer> unsortMap, final boolean order)
    {

        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>()
        {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
