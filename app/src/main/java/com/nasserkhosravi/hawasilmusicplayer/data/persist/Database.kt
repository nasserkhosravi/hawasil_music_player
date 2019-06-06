package com.nasserkhosravi.hawasilmusicplayer.data.persist

import android.annotation.SuppressLint
import androidx.annotation.IntDef
import androidx.palette.graphics.Palette
import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

@Database(entities = [SwatchTable::class], version = 1)
abstract class MusicPlayerDatabase : RoomDatabase() {
    abstract fun getSongDao(): SongDao
}

@SuppressLint("CheckResult")
fun MusicPlayerDatabase.clearAllTabsOnSchedulersIO() {
    Observable.just(1).subscribeOn(Schedulers.io())
        .subscribe {
            clearAllTables()
        }
}

@Dao
interface SongDao {

    @Query("select * from Swatch where songId = :songId")
    fun getSwatch(songId: Long): Single<SwatchTable>

    @Insert
    fun insertSwatch(model: SwatchTable): Completable
}

@Entity(tableName = "Swatch")
class SwatchTable(
    @ColumnInfo
    var songId: Long,
    @ColumnInfo
    var population: Int,
    @ColumnInfo
    var color: Int,
    @ColumnInfo
    @Type
    var type: Int
) {
    @ColumnInfo
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun toSwatch() = Palette.Swatch(color, population)

    companion object {
        const val MUTED = 0
    }

    @IntDef(MUTED)
    annotation class Type
}
