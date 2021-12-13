package com.loan555.kisdapplication2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.loan555.kisdapplication2.model.DanhSachTreEm

@Database(
    entities = [NotificationEntity::class, KidsEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
    abstract fun kidsDao(): KidsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_db"
                    )
                        .build()
                INSTANCE = instance
                instance
            }
        }
    }
}