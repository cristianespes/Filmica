package com.celapps.filmica.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [Film::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    // Clase que nos va a permitir acceder a los Dao
    abstract fun filmDao(): FilmDao
}