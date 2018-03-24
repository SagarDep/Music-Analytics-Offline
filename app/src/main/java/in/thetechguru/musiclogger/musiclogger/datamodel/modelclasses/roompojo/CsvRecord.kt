package `in`.thetechguru.musiclogger.musiclogger.datamodel.modelclasses.roompojo

/**
 * Created by abami on 24-Mar-18.
 */
data class CsvRecord (val package_name: String
                      , val song_name: String
                      , val artist_name: String
                      , val album_name: String
                      , val genre_name: String
                      , val total_duration: Long
                      , val approx_played_for: Long
                      , val started_playing_at: Long) {

    override fun toString(): String {
        return "$package_name#$song_name#$artist_name#$album_name#$genre_name#$total_duration#$approx_played_for#$started_playing_at"
    }

    companion object {
        fun getCsvHeaderString():String{
            return "package_name#song_name#artist_name#album_name#genre_name#total_duration#approx_played_for#started_playing_at"
        }
    }
}