package `in`.thetechguru.musiclogger.musiclogger.model

import android.media.MediaMetadata

/**
 * Created by abami on 23-Feb-18.
 * custom model class which will be used to keep track of currently playing media
 */
data class MediaSessionMetaData(var title:String? = ""
                                , var artist:String? = ""
                                , var album: String? = ""
                                , var genre: String? = ""
                                , var alt_artist: String? = ""
                                , var duration: Long = 0L
                                , var album_art_uri: String? = ""
                                , var track_number: Long? = 0L
                                , var number_of_tracks: Long? = 0L
                                , var started_playing_at: Long? = 0L//custom field tried to generate from media session callbacks
                                , var approx_played_for: Long? = 0L)//custom field tried to generate from media session callbacks
{
    /*
    Initially build the object by  raw mediametadata object
    After that start time and time for which media was played will be deduced by notification listener and scrobbler service
     */
    constructor(meta_data: MediaMetadata):this(){
        this.title = meta_data.getString(MediaMetadata.METADATA_KEY_TITLE)
        this.artist = meta_data.getString(MediaMetadata.METADATA_KEY_ARTIST)
        this.album = meta_data.getString(MediaMetadata.METADATA_KEY_ALBUM)
        this.genre = meta_data.getString(MediaMetadata.METADATA_KEY_GENRE)
        this.alt_artist = meta_data.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
        this.album_art_uri = meta_data.getString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI)
        this.duration = meta_data.getLong(MediaMetadata.METADATA_KEY_DURATION)
        this.track_number = meta_data.getLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER)
        this.number_of_tracks = meta_data.getLong(MediaMetadata.METADATA_KEY_NUM_TRACKS)
    }

    /*
    simple sanitary check to make sure data is good enough to be put in db
    @todo refine this later
     */
    fun isValidRecord(): Boolean{
        return !(title?.isEmpty())!!
    }
}