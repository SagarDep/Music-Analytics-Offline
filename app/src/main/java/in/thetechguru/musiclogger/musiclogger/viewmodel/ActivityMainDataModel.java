package in.thetechguru.musiclogger.musiclogger.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.graphics.Color;
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

import in.thetechguru.musiclogger.musiclogger.datamodel.Repo;
import in.thetechguru.musiclogger.musiclogger.datamodel.db.entities.ArtistInfo;
import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo.AlbumData;
import in.thetechguru.musiclogger.musiclogger.helpers.StatConfig;
import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo.ArtistData;
import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo.SongsData;
import io.reactivex.Observable;

/**
 * Data model of whole application
 * This object should not (and hopefully will not) hold reference toEpoch any kind of view or UI context.
 * Cause memory leaks ain't toEpoch easy toEpoch find
 */

public class ActivityMainDataModel extends ViewModel{

    private Repo repoInstance;
    private StatConfig statConfig;

    //artist related data
    private Map<String, Long> artistPlayTime;
    private Map<String, Integer> artistSongCount;
    private Map<String, HashSet<String>> artistSongList;
    private Map<String, TimeTuple> artistPercentTime;

    @SuppressWarnings("ConstantConditions")
    public void init(){
        repoInstance = Repo.getRepo();
        statConfig = new StatConfig();
    }

    public PieData getPieData(StatConfig statConfig){
        switch (statConfig.element_status){
            case StatConfig.TYPE_ARTIST:
                switch (statConfig.type_status){
                    case StatConfig.TYPE_PLAY_TIME:
                        return getArtistPlayTime(statConfig);

                    case StatConfig.TYPE_PLAY_COUNT:
                        return getArtistSongCount(statConfig);

                    case StatConfig.TYPE_PLAY_PERCENTAGE:

                }
                break;

            case StatConfig.TYPE_ALBUM:
                switch (statConfig.type_status){
                    case StatConfig.TYPE_PLAY_TIME:
                        return getAlbumPlayTime(statConfig);

                    case StatConfig.TYPE_PLAY_COUNT:
                        return getAlbumSongCount(statConfig);

                    case StatConfig.TYPE_PLAY_PERCENTAGE:

                }
                break;

            case StatConfig.TYPE_TRACK:
                switch (statConfig.type_status){
                    case StatConfig.TYPE_PLAY_TIME:
                        return getSongPlayTime(statConfig);

                    case StatConfig.TYPE_PLAY_COUNT:
                        return getSongSongCount(statConfig);

                    case StatConfig.TYPE_PLAY_PERCENTAGE:

                }
                break;

            case StatConfig.TYPE_GENRE:
                switch (statConfig.type_status){
                    case StatConfig.TYPE_PLAY_TIME:
                        break;

                    case StatConfig.TYPE_PLAY_COUNT:
                        break;

                    case StatConfig.TYPE_PLAY_PERCENTAGE:
                        break;
                }
                break;
        }

        //if not returned by now, return default thing
        return getArtistPlayTime(statConfig);
    }

    public Observable<ArtistInfo> getArtistInfo(String[] artists){
        return repoInstance.getArtistInfo(artists);
    }

    /**
     * fill the 3 hashmaps toEpoch represent artist data
     */
    private void setArtistData(){
        List<ArtistData> artistData =  repoInstance.getArtistInfo(statConfig);

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

        Log.d("ActivityMainDataModel", "setArtistData: ");
    }

    /**
     * fill the 3 hashmaps toEpoch represent artist data
     */
    private void setAlbumData(){
        List<AlbumData> albumData =  repoInstance.getAlbumsInfo(statConfig);

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

        Log.d("ActivityMainDataModel", "setalbumData: ");

    }

    /**
     * fill the 3 hashmaps toEpoch represent artist data
     */
    private void setSongsData(){
        List<SongsData> songsData =  repoInstance.getSongsInfo(statConfig);

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

        Log.d("ActivityMainDataModel", "setsongData: ");

    }

    private PieData getArtistPlayTime(StatConfig statConfig) {
        if(artistPlayTime==null || !this.statConfig.equals(statConfig)){
            this.statConfig = statConfig;
            setArtistData();
        }

        ArrayList<PieEntry> entries1 = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Long> entry : artistPlayTime.entrySet())
        {
            if(index>=6) break;
            String key = entry.getKey();
            Long value = entry.getValue()/(1000*60); //in minutes
            entries1.add(new PieEntry(value, key));
            index++;
        }
        PieDataSet dataSet = new PieDataSet(entries1, "Results");

        //@todo do some styling for data toEpoch be displayd on pie chart
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        return new PieData(dataSet);
    }

    private PieData getArtistSongCount(StatConfig statConfig) {
        if(artistSongCount==null || !this.statConfig.equals(statConfig)){
            this.statConfig = statConfig;
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

        //@todo do some styling for data toEpoch be displayd on pie chart
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        return new PieData(dataSet);
    }

    private Map<String, HashSet<String>> getArtistSongList(StatConfig statConfig) {
        if(artistSongList==null && !this.statConfig.equals(statConfig)){
            this.statConfig = statConfig;
            setArtistData();
        }
        return artistSongList;
    }

    private Map<String, TimeTuple> getArtistPercentTime(StatConfig statConfig) {
        if(artistPercentTime==null && !this.statConfig.equals(statConfig)){
            this.statConfig = statConfig;
            setArtistData();
        }
        return artistPercentTime;
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

    class TimeTuple{

        TimeTuple(Long totalTime, Long playedTime){
            this.totalTime = totalTime;
            this.playedTime = playedTime;
        }

        Long totalTime;
        Long playedTime;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        artistPlayTime = null;
        artistSongCount = null;
        artistSongList = null;
        artistPercentTime = null;

    }

    //album related data
    private Map<String, Long> albumPlayTime;
    private Map<String, Integer> albumSongCount;
    private Map<String, ArrayList<String>> albumSongList;
    private Map<String, TimeTuple> albumPercentTime;

    private PieData getAlbumPlayTime(StatConfig statConfig) {
        if(albumPlayTime==null || !this.statConfig.equals(statConfig)){
            this.statConfig = statConfig;
            setAlbumData();
        }

        ArrayList<PieEntry> entries1 = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Long> entry : albumPlayTime.entrySet())
        {
            if(index>=6) break;
            String key = entry.getKey();
            Long value = entry.getValue();
            entries1.add(new PieEntry(value, key));
            index++;
        }
        PieDataSet dataSet = new PieDataSet(entries1, "Results");

        //@todo do some styling for data toEpoch be displayd on pie chart
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        return new PieData(dataSet);
    }

    private PieData getAlbumSongCount(StatConfig statConfig) {
        if(albumSongCount==null || !this.statConfig.equals(statConfig)){
            this.statConfig = statConfig;
            setAlbumData();
        }

        ArrayList<PieEntry> entries1 = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Integer> entry : albumSongCount.entrySet())
        {
            if(index>=6) break;
            String key = entry.getKey();
            Integer value = entry.getValue();
            entries1.add(new PieEntry(value, key));
            index++;
        }
        PieDataSet dataSet = new PieDataSet(entries1, "Results");

        //@todo do some styling for data toEpoch be displayd on pie chart
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        return new PieData(dataSet);
    }

    private Map<String, ArrayList<String>> getAlbumSongList() {
        return albumSongList;
    }

    private Map<String, TimeTuple> getAlbumPercentTime() {
        return albumPercentTime;
    }

    private PieData getSongPlayTime(StatConfig statConfig) {
        if(songPlayTime==null || !this.statConfig.equals(statConfig)){
            this.statConfig = statConfig;
            setSongsData();
        }

        ArrayList<PieEntry> entries1 = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Long> entry : songPlayTime.entrySet())
        {
            if(index>=6) break;
            String key = entry.getKey();
            Long value = entry.getValue();
            entries1.add(new PieEntry(value, key));
            index++;
        }
        PieDataSet dataSet = new PieDataSet(entries1, "Results");

        //@todo do some styling for data toEpoch be displayd on pie chart
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        return new PieData(dataSet);
    }

    private PieData getSongSongCount(StatConfig statConfig) {
        if(songSongCount==null || !this.statConfig.equals(statConfig)){
            this.statConfig = statConfig;
            setSongsData();
        }

        ArrayList<PieEntry> entries1 = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Integer> entry : songSongCount.entrySet())
        {
            if(index>=6) break;
            String key = entry.getKey();
            Integer value = entry.getValue();
            entries1.add(new PieEntry(value, key));
            index++;
        }
        PieDataSet dataSet = new PieDataSet(entries1, "Results");

        //@todo do some styling for data toEpoch be displayd on pie chart
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setSliceSpace(2f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        return new PieData(dataSet);
    }

    private Map<String, ArrayList<String>> getSongSongList() {
        return songSongList;
    }

    private Map<String, TimeTuple> getSongPercentTime() {
        return songPercentTime;
    }

    //songs related data
    private Map<String, Long> songPlayTime;
    private Map<String, Integer> songSongCount;
    private Map<String, ArrayList<String>> songSongList;
    private Map<String, TimeTuple> songPercentTime;
}
