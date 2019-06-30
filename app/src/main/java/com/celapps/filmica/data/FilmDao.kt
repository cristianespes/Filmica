package com.celapps.filmica.data

import androidx.room.*

@Dao
interface FilmDao {

    @Query("SELECT * FROM film")
    fun getFilms(): List<Film>

    // Dentro del paréntesis, podemos decir que ocurre si ya existe esa película dentro de la BBDD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilm(film: Film)

    @Update
    fun updateFilm(film: Film)

    @Delete
    fun deleteFilm(film: Film)
}