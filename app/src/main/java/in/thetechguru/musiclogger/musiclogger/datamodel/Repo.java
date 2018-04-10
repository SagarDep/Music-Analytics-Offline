package in.thetechguru.musiclogger.musiclogger.datamodel;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import in.thetechguru.musiclogger.musiclogger.MyApp;
import in.thetechguru.musiclogger.musiclogger.datamodel.api.ApiUtil;
import in.thetechguru.musiclogger.musiclogger.datamodel.api.ArtistInfoAPIService;
import in.thetechguru.musiclogger.musiclogger.datamodel.db.ArtistInfoDAO;
import in.thetechguru.musiclogger.musiclogger.datamodel.db.ArtistInfoDB;
import in.thetechguru.musiclogger.musiclogger.datamodel.db.MusicRecordsDB;
import in.thetechguru.musiclogger.musiclogger.datamodel.db.MusicRecordsDao;
import in.thetechguru.musiclogger.musiclogger.datamodel.db.entities.Album;
import in.thetechguru.musiclogger.musiclogger.datamodel.db.entities.Artist;
import in.thetechguru.musiclogger.musiclogger.datamodel.db.entities.ArtistInfo;
import in.thetechguru.musiclogger.musiclogger.datamodel.db.entities.Genre;
import in.thetechguru.musiclogger.musiclogger.datamodel.db.entities.MusicRecord;
import in.thetechguru.musiclogger.musiclogger.datamodel.db.entities.Song;
import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.apipojo.ArtistLastFm;
import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.apipojo.ArtistWrapper;
import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo.AlbumData;
import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo.ArtistData;
import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo.CsvRecord;
import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo.MediaSessionMetaData;
import in.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo.SongsData;
import in.thetechguru.musiclogger.musiclogger.helpers.StatConfig;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
 */

/**
 * Created by abami on 18-Mar-18.
 * Repository singleton for globally accessing data source
 */

public class Repo {

    private static Repo instance;
    private MusicRecordsDao dbDao;
    private ArtistInfoDAO artInfoDbDao;
    private ArtistInfoAPIService artistInfoAPIService;

    @SuppressWarnings("ConstantConditions")
    private Repo(){
        artInfoDbDao = ArtistInfoDB.getInstance(MyApp.getInstance()).ArtistInfoDAO();
        dbDao = MusicRecordsDB.getInstance(MyApp.getInstance()).MusicRecordDAO();
        artistInfoAPIService = ApiUtil.getAPIService();
    }

    public static Repo getRepo(){
        if(instance==null) instance = new Repo();
        return instance;
    }

    /**
     * push music record in table
     * @param mediaSessionMetaData - everything we are going toEpoch need toEpoch make entry
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

        Log.d("ActivityMainDataModel", "pushRecord: " + musicRecord);

        //insert actual record
        dbDao.insert(musicRecord);
    }

    public List<ArtistData> getArtistInfo(StatConfig statConfig){
        if(statConfig.interval_status == StatConfig.LIFETIME) {
            return dbDao.getArtistInfo();
        }else {
            return dbDao.getArtistInfo(statConfig.fromEpoch, statConfig.toEpoch);
        }
    }

    public List<AlbumData> getAlbumsInfo(StatConfig statConfig){
        if(statConfig.interval_status == StatConfig.LIFETIME) {
            return dbDao.getAlbumInfo();
        }else {
            return dbDao.getAlbumInfo(statConfig.fromEpoch, statConfig.toEpoch);
        }
    }

    public List<SongsData> getSongsInfo(StatConfig statConfig){
        if(statConfig.interval_status == StatConfig.LIFETIME) {
            return dbDao.getSongsInfo();
        }else {
            return dbDao.getSongsInfo(statConfig.fromEpoch, statConfig.toEpoch);
        }
    }

    public List<CsvRecord> getCSVRecords(){
        return dbDao.getCsvData();
    }

    public Observable<ArtistInfo> getArtistInfo(final String[] artists){


        return Observable.create(new ObservableOnSubscribe<ArtistInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<ArtistInfo> emitter) throws Exception {

                for(String artist: artists ) {
                    List<ArtistInfo> artistInfos = artInfoDbDao.getArtistLastFMBio(artist);

                    if (artistInfos.size() != 0) {
                        emitter.onNext(artistInfos.get(0));
                        continue;
                    }

                    Response<ArtistWrapper> response = artistInfoAPIService.getArtist(artist).execute();
                    if (!response.isSuccessful() || response.body() == null) {
                        emitter.onError(new Throwable("Invalid Response"));
                        return;
                    }
                    ArtistLastFm artistLastFm = response.body().getArtist();
                    ArtistInfo artistInfo = new ArtistInfo();
                    artistInfo.original_artist = artist;
                    artistInfo.corrected_artist = artistLastFm.getName();
                    artistInfo.artist_image_big = artistLastFm.getImage().get(3).getText();
                    artistInfo.artist_image_thumb = artistLastFm.getImage().get(0).getText();
                    artistInfo.artist_info = artistLastFm.getBio().getContent();
                    artistInfo.artist_url = artistLastFm.getUrl();
                    artistInfo.mbid = artistLastFm.getMbid();
                    artistInfo.tags = artistLastFm.getTags().getTag();
                    emitter.onNext(artistInfo);
                }

                emitter.onComplete();
            }
        });
    }
}
