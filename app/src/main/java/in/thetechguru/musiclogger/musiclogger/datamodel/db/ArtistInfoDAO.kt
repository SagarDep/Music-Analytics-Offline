package `in`.thetechguru.musiclogger.musiclogger.datamodel.db

import `in`.thetechguru.musiclogger.musiclogger.datamodel.db.entities.ArtistInfo
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single

/**
 * Created by abami on 24-Mar-18.
 */

@Dao
interface ArtistInfoDAO {

    @Query("SELECT mbid, original_artist, corrected_artist, artist_info, artist_url" +
            ", artist_image_thumb, artist_image_big, tags from artist_info WHERE original_artist = :artist")
    fun getArtistLastFMBio(artist: String): List<ArtistInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(artistInfo: ArtistInfo)

}