package `in`.thetechguru.musiclogger.musiclogger.data.model_classes

import android.media.MediaMetadata

/**
 * Created by abami on 23-Feb-18.
 * custom model class which will be used to keep track of currently playing media
 */
data class MediaSessionMetaData(var package_name: String = ""
                                ,var title:String = ""
                                , var artist:String = ""
                                , var album: String = ""
                                , var genre: String = ""
                                , var total_duration: Long = 0L
                                , var started_playing_at: Long = 0L//custom field tried to generate from media session callbacks
                                , var approx_played_for: Long = 0L //custom field tried to generate from media session callbacks
                                )
{

    val MINIMUM_DURATION_FOR_VALID_RECORD: Long = 20000   // records less than 20 seconds invalid
    /*
    Initially build the object by  raw mediametadata object
    After that start time and time for which media was played will be deduced by notification listener and scrobbler service
     */
    constructor(meta_data: MediaMetadata, package_name: String):this(){
        this.title = meta_data.getString(MediaMetadata.METADATA_KEY_TITLE)+""
        this.artist = meta_data.getString(MediaMetadata.METADATA_KEY_ARTIST)+""
        if(artist.isEmpty()) {
            this.artist = meta_data.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST) + ""
        }
        this.album = meta_data.getString(MediaMetadata.METADATA_KEY_ALBUM)+""
        this.genre = meta_data.getString(MediaMetadata.METADATA_KEY_GENRE)+""
        this.total_duration = meta_data.getLong(MediaMetadata.METADATA_KEY_DURATION)
        this.started_playing_at = System.currentTimeMillis()
        this.package_name = package_name+""
    }

    /**
     * set approximate play time before pushing record to DB
     * Its done by subtracting current time from the time when media was started
     * It is approximate and I need to figure out way to increase its accuracy
     */
    private fun setApproxPlayTime(){
        val current_time = System.currentTimeMillis()

        if ((current_time.minus(started_playing_at)) >= total_duration) {
            approx_played_for = total_duration
        } else {
            approx_played_for = current_time.minus(started_playing_at)
        }
    }

    /*
    simple sanitary check to make sure ArtistData is good enough to be put in db
    @todo refine this later
     */
    fun isValidRecord(): Boolean{
        setApproxPlayTime()
        return !(artist.isEmpty() || (total_duration<=0 && approx_played_for < MINIMUM_DURATION_FOR_VALID_RECORD))
    }

    override fun toString(): String {
        return "$title : $artist : Approx played for : $approx_played_for"
    }
}