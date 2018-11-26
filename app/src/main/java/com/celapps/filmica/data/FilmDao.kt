package com.celapps.filmica.data

import android.arch.persistence.room.*

@Dao
interface FilmDao {
    // Dentro del paréntesis, podemos decir que ocurre si ya existe esa película dentro de la BBDD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilm(film: Film)

    @Query("SELECT * FROM film")
    fun getFilms(): List<Film>

    @Delete
    fun deleteFilm(film: Film)
}