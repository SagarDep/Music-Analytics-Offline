package `in`.thetechguru.musiclogger.musiclogger.data.db

import `in`.thetechguru.musiclogger.musiclogger.data.db.entities.*
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

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

@Database(entities = arrayOf(MusicRecord::class, Artist::class, Album::class, Genre::class, Song::class)
        , version = 1)
abstract class MusicRecordsDB : RoomDatabase() {

    abstract fun MusicRecordDAO():MusicRecordsDao?


    //we are making it singleton
    //maybe in future we may change this
    //for now its singleton
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