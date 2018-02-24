package `in`.thetechguru.musiclogger.musiclogger.data_view_model.db

import `in`.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.*
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.IGNORE
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

/**
 * Created by abami on 22-Feb-18.
 */
@Dao
interface MusicRecordsDao {
    @Query("SELECT * from music_records")
    fun getAll(): List<MusicRecord>

    @Query("SELECT * from songs")
    fun getAllSongs(): List<Song>

    @Query("SELECT * from artists")
    fun getAllArtists(): List<Artist>

    @Query("SELECT * from genres")
    fun getAllGenres(): List<Genre>

    @Query("SELECT * from albums")
    fun getAllAlbums(): List<Album>

    @Insert(onConflict = IGNORE)
    fun insert(record: MusicRecord):Long

    @Insert(onConflict = REPLACE)
    fun insert(song: Song):Long

    @Query("SELECT id from songs where song_name = :song_name")
    fun getSongId(song_name:String):Long

    @Insert(onConflict = REPLACE)
    fun insert(artist: Artist):Long

    @Query("SELECT id from artists where artist_name = :artist_name")
    fun getArtistId(artist_name:String):Long

    @Insert(onConflict = REPLACE)
    fun insert(album: Album):Long

    @Query("SELECT id from albums where album_name = :album_name")
    fun getAlbumId(album_name:String):Long

    @Insert(onConflict = REPLACE)
    fun insert(genre: Genre):Long

    @Query("SELECT id from genres where genre_name = :genre_name")
    fun getGenreId(genre_name:String):Long
}