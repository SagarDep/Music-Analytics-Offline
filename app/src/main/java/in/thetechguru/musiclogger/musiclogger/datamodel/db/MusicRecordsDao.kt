package `in`.thetechguru.musiclogger.musiclogger.datamodel.db

import `in`.thetechguru.musiclogger.musiclogger.datamodel.db.entities.*
import `in`.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo.AlbumData
import `in`.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo.ArtistData
import `in`.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo.CsvRecord
import `in`.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo.SongsData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.*
import android.arch.persistence.room.Query

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

/**
This file is going to be Heart of this project
 */

@Dao
interface MusicRecordsDao {

    //request bulk
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

    //insert onConflict strategy is REPLACE, but it won't be used anyway
    //before inserting values in parent tables, check has been placed to make sure there is no record
    //because not doing so replaces record with new auto incremented id and is cascaded to all child tables
    //we definitely don't want that because if tomorrow our database gets bigger, every time song of same artist will be played
    //replace changes will be cascaded to all child tables, we don't need that kind of shit in our project

    @Insert(onConflict = REPLACE)
    fun insert(record: MusicRecord):Long

    @Insert(onConflict = REPLACE)
    fun insert(song: Song):Long

    @Insert(onConflict = REPLACE)
    fun insert(artist: Artist):Long

    @Insert(onConflict = REPLACE)
    fun insert(album: Album):Long

    @Insert(onConflict = REPLACE)
    fun insert(genre: Genre):Long

    //query ArtistData
    @Query("SELECT id from genres where genre_name = :genre_name")
    fun getGenreId(genre_name:String):Long

    @Query("SELECT id from songs where song_name = :song_name")
    fun getSongId(song_name:String):Long

    @Query("SELECT id from artists where artist_name = :artist_name")
    fun getArtistId(artist_name:String):Long

    @Query("SELECT id from albums where album_name = :album_name")
    fun getAlbumId(album_name:String):Long

    @Query("SELECT artists.artist_name, music_records.approx_played_for, music_records.total_duration , songs.song_name from music_records " +
            "JOIN artists on music_records.artist_id = artists.id  " +
            "JOIN songs on music_records.song_id = songs.id")
    fun getArtistInfo():List<ArtistData>

    @Query("SELECT artists.artist_name, music_records.approx_played_for, music_records.total_duration , songs.song_name from music_records " +
            "JOIN artists on music_records.artist_id = artists.id  " +
            "JOIN songs on music_records.song_id = songs.id " +
            "WHERE music_records.started_playing_at > :from and music_records.started_playing_at < :to" )
    fun getArtistInfo(from:Long, to:Long):List<ArtistData>

    @Query("SELECT albums.album_name, music_records.approx_played_for, music_records.total_duration , songs.song_name from music_records " +
            "JOIN albums on music_records.album_id = albums.id  " +
            "JOIN songs on music_records.song_id = songs.id")
    fun getAlbumInfo():List<AlbumData>

    @Query("SELECT albums.album_name, music_records.approx_played_for, music_records.total_duration , songs.song_name from music_records " +
            "JOIN albums on music_records.album_id = albums.id  " +
            "JOIN songs on music_records.song_id = songs.id " +
            "WHERE music_records.started_playing_at > :from and music_records.started_playing_at < :to" )
    fun getAlbumInfo(from:Long, to:Long):List<AlbumData>

    @Query("SELECT artists.artist_name, music_records.approx_played_for, music_records.total_duration , songs.song_name from music_records " +
            "JOIN artists on music_records.artist_id = artists.id  " +
            "JOIN songs on music_records.song_id = songs.id")
    fun getSongsInfo():List<SongsData>

    @Query("SELECT artists.artist_name, music_records.approx_played_for, music_records.total_duration , songs.song_name from music_records " +
            "JOIN artists on music_records.artist_id = artists.id  " +
            "JOIN songs on music_records.song_id = songs.id " +
            "WHERE music_records.started_playing_at > :from and music_records.started_playing_at < :to" )
    fun getSongsInfo(from:Long, to:Long):List<SongsData>

    @Query("SELECT music_records.package_name, songs.song_name, " +
            "artists.artist_name, albums.album_name, genres.genre_name, " +
            "music_records.total_duration, music_records.approx_played_for, music_records.started_playing_at " +
            "from music_records " +
            "JOIN artists on music_records.artist_id = artists.id  " +
            "JOIN songs on music_records.song_id = songs.id " +
            "JOIN albums on music_records.album_id = albums.id " +
            "JOIN genres on music_records.genre_id = genres.id " )
    fun getCsvData():List<CsvRecord>

    //nuke all tables
    @Query("DELETE from music_records")
    fun nukeRecords()

    @Query("DELETE from songs")
    fun nukeSongs()

    @Query("DELETE from artists")
    fun nukeArtists()

    @Query("DELETE from genres")
    fun nukeGenres()

    @Query("DELETE from albums")
    fun nukeAlbums()
}