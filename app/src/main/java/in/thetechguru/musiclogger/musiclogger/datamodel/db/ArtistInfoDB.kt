package `in`.thetechguru.musiclogger.musiclogger.datamodel.db

import `in`.thetechguru.musiclogger.musiclogger.datamodel.db.entities.*
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

/**
 * Created by abami on 24-Mar-18.
 */

@Database(entities = arrayOf(ArtistInfo::class)
        , version = 2)
@TypeConverters(Converter::class)
abstract class ArtistInfoDB: RoomDatabase() {

    abstract fun ArtistInfoDAO():ArtistInfoDAO?

    companion object {
        private var instance: ArtistInfoDB? = null

        @JvmStatic fun getInstance(context: Context) :ArtistInfoDB?{
            if(instance == null){
                synchronized(MusicRecordsDB::class){
                    instance = Room.databaseBuilder(context.applicationContext
                            ,ArtistInfoDB::class.java, "music_record.db")
                            .fallbackToDestructiveMigration()
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