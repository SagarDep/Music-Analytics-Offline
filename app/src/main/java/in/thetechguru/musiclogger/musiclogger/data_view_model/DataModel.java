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
 */

public class DataModel extends ViewModel{

    private MusicRecordsDao dbDao;

    @SuppressWarnings("ConstantConditions")
    public void init(){
        if(dbDao==null){
            dbDao = MusicRecordsDB.getInstance(MyApp.getInstance()).MusicRecordDAO();
        }
    }

    public void pushRecord(@NonNull MediaSessionMetaData mediaSessionMetaData){

        Song song = new Song(mediaSessionMetaData.getTitle());

        String artist_name = mediaSessionMetaData.getArtist();
        if(artist_name.equals(""))
            artist_name = mediaSessionMetaData.getAlt_artist();
        Artist artist = new Artist(artist_name);

        Album album = new Album(mediaSessionMetaData.getAlbum());

        Genre genre = new Genre(mediaSessionMetaData.getGenre());

        MusicRecord musicRecord = new MusicRecord(mediaSessionMetaData);

        Log.d("DataModel", "" + dbDao.insert(song));
        Log.d("DataModel", "" + dbDao.insert(artist));
        Log.d("DataModel", "" + dbDao.insert(album));
        Log.d("DataModel", "" + dbDao.insert(genre));

        //insert song, artist, genre and song
        musicRecord.song_id = dbDao.getSongId(song.song_name);
        musicRecord.artist_id = dbDao.getArtistId(artist.artist_name);
        musicRecord.album_id = dbDao.getAlbumId(album.album_name);
        musicRecord.genre_id = dbDao.getGenreId(genre.genre_name);

        Log.d("DataModel", "pushRecord: " + musicRecord);

        //insert actual record
        dbDao.insert(musicRecord);
    }

}
