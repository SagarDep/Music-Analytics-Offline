package `in`.thetechguru.musiclogger.musiclogger.db.entities

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query

/**
 * Created by abami on 22-Feb-18.
 */
@Dao
interface Daao {


    @Query("SELECT * from music_records")
    fun getAll(): List<MusicRecord>

}