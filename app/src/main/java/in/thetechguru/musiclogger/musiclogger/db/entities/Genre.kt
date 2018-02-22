package `in`.thetechguru.musiclogger.musiclogger.db.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by abami on 22-Feb-18.
 */
@Entity(tableName = "genres")
data class Genre (@PrimaryKey(autoGenerate = true) var id: Long,
             @ColumnInfo(name = "genre_name") var genre_name: String) {
}