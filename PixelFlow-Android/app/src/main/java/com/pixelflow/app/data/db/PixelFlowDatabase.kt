package com.pixelflow.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pixelflow.app.data.entity.AppSettings
import com.pixelflow.app.data.entity.HistoryEntry

@Database(
    entities = [HistoryEntry::class, AppSettings::class],
    version = 1,
    exportSchema = false
)
abstract class PixelFlowDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile private var INSTANCE: PixelFlowDatabase? = null

        fun get(context: Context): PixelFlowDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PixelFlowDatabase::class.java,
                    "pixelflow.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}
