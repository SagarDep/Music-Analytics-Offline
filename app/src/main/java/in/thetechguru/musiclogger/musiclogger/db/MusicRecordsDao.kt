package `in`.thetechguru.musiclogger.musiclogger.db

import `in`.thetechguru.musiclogger.musiclogger.db.entities.MusicRecord
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

/**
 * Created by abami on 22-Feb-18.
 */
@Dao
interface MusicRecordsDao {

    @Query("SELECT * from music_records")
    fun getAll(): List<MusicRecord>

    @Insert(onConflict = REPLACE)
    fun insert(record: MusicRecord)
}