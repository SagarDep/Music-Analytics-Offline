package `in`.thetechguru.musiclogger.musiclogger.data_view_model.model_classes

import android.media.MediaMetadata
import android.util.Log

/**
 * Created by abami on 23-Feb-18.
 * custom model class which will be used to keep track of currently playing media
 */
data class MediaSessionMetaData(var package_name: String = ""
                                ,var title:String = ""
                                , var artist:String = ""
                                , var album: String = ""
                                , var genre: String = ""
                                , var alt_artist: String = ""
                                , var total_duration: Long = 0L
                                , var started_playing_at: Long = 0L//custom field tried to generate from media session callbacks
                                , var approx_played_for: Long = 0L //custom field tried to generate from media session callbacks
                                )
{
    /*
    Initially build the object by  raw mediametadata object
    After that start time and time for which media was played will be deduced by notification listener and scrobbler service
     */
    constructor(meta_data: MediaMetadata, package_name: String):this(){
        this.title = meta_data.getString(MediaMetadata.METADATA_KEY_TITLE)+""
        this.artist = meta_data.getString(MediaMetadata.METADATA_KEY_ARTIST)+""
        this.album = meta_data.getString(MediaMetadata.METADATA_KEY_ALBUM)+""
        this.genre = meta_data.getString(MediaMetadata.METADATA_KEY_GENRE)+""
        this.alt_artist = meta_data.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)+""
        this.total_duration = meta_data.getLong(MediaMetadata.METADATA_KEY_DURATION)
        this.started_playing_at = System.currentTimeMillis()
        this.package_name = package_name+""
    }

    fun setApproxPlayTime(){
        val current_time = System.currentTimeMillis()
        if ((current_time.minus(started_playing_at)) >= total_duration) {
            approx_played_for = total_duration
        } else {
            approx_played_for = current_time.minus(started_playing_at)
        }
    }

    /*
    simple sanitary check to make sure data is good enough to be put in db
    @todo refine this later
     */
    fun isValidRecord(): Boolean{
        return !(title.isEmpty())
    }

    override fun toString(): String {
        return "$title : $artist : Approx play time : $approx_played_for"
    }
}