package com.nagl.smartgeoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nagl.smartgeoapp.database.dao.CoordinateSystemDao
import com.nagl.smartgeoapp.database.dao.PointsDao
import com.nagl.smartgeoapp.database.entity.CoordinateSystem
import com.nagl.smartgeoapp.database.entity.Point

@Database(
    entities = [
        CoordinateSystem::class,
        Point::class
    ],
    version = 3,
    exportSchema = true
)
abstract class LocalDB : RoomDatabase() {
    abstract fun getCoordinateSystemDao() : CoordinateSystemDao
    abstract fun getPointsDao(): PointsDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDB? = null

        fun getDatabase(context: Context): LocalDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDB::class.java,
                    "NoteDatabase"
                )
                    .createFromAsset("NoteDatabase.db")
                    .addMigrations(versionMigration12, versionMigration23)
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        val versionMigration12 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE CoordinateSystem ADD COLUMN type TEXT NOT NULL DEFAULT 'cart'")
            }
        }

        val versionMigration23 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS Point (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `date` INTEGER NOT NULL, `coordinate_firstCoordinate` REAL NOT NULL, `coordinate_secondCoordinate` REAL NOT NULL, `coordinate_thirdCoordinate` REAL NOT NULL, `coordinate_format` TEXT NOT NULL)")
            }
        }
    }
}