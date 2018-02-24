package `in`.thetechguru.musiclogger.musiclogger.data_view_model.db

import `in`.thetechguru.musiclogger.musiclogger.data_view_model.db.entities.*
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Created by abami on 22-Feb-18.
 */
@Database(entities = arrayOf(MusicRecord::class, Artist::class, Album::class, Genre::class, Song::class)
        , version = 1)
abstract class MusicRecordsDB : RoomDatabase() {

    abstract fun MusicRecordDAO():MusicRecordsDao?

    companion object {
        private var instance: MusicRecordsDB? = null

        @JvmStatic fun getInstance(context: Context) :MusicRecordsDB?{
            if(instance == null){
                synchronized(MusicRecordsDB::class){
                    instance = Room.databaseBuilder(context.applicationContext
                            ,MusicRecordsDB::class.java, "music_record.db")
                            .build()
                }
            }
            return instance
        }

        fun destroyInstance(){
            instance = null
        }
    }

}