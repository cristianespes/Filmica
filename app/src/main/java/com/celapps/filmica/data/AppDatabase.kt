package com.celapps.filmica.data

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

@Database(entities = [Film::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    // Clase que nos va a permitir acceder a los Dao
    abstract fun filmDao(): FilmDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Film ADD COLUMN backdrop TEXT NOT NULL DEFAULT ''")
    }
}