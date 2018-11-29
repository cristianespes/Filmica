package com.celapps.filmica.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

@Entity
data class Film(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    @ColumnInfo (name = "vote_rating") var voteRating: Double = 0.0,
    var title: String = "",
    var genre: String = "",
    var release: String = "",
    var overview: String = "",
    var poster: String = ""
) {

    // Si intento ejecutar una creación sin argumento, ignorar esa insercción a la BBDD
    @Ignore
    constructor(): this("")

    fun getPosterUrl() : String {
        return "$BASE_POSTER_URL$poster"
    }

    companion object {
        // Método para parsear el array de resultados y devolver un listado de películas
        fun parseFilms(response: JSONObject) : MutableList<Film> {
            val filmsArray = response.getJSONArray("results")
            val films = mutableListOf<Film>()

            for (i in 0..(filmsArray.length() - 1)) {
                val film = parseFilm(filmsArray.getJSONObject(i))
                films.add(film)
            }

            return films
        }

        // Método para parsear el objeto Json de la película y devolver un objeto película
        fun parseFilm(jsonFilm: JSONObject) : Film {
            return Film(
                id = jsonFilm.getInt("id").toString(),
                // title es opcional
                title = jsonFilm.optString("title", jsonFilm.optString("name", "Unnamed")),
                overview = jsonFilm.optString("overview", ""),
                voteRating = jsonFilm.optDouble("vote_average", 5.0),
                // release_date es opcional
                release = jsonFilm.optString("release_date", "Undated"),
                // poster es opcional
                poster = jsonFilm.optString("poster_path", jsonFilm.optString("logo_path", "")),
                genre = if (jsonFilm.length() > 3) parseGenres(jsonFilm.optJSONArray("genre_ids")) else ""
            )
        }

        private fun parseGenres(genresArray: JSONArray) : String {

            val genres = mutableListOf<String>()

            for ( i in 0..(genresArray.length() - 1) ) {
                val genreId = genresArray.getInt(i)
                val genre = ApiConstants.genres[genreId] ?: ""
                genres.add(genre)
            }

            // acc: acumulador
            return genres.reduce { acc, genre -> "$acc | $genre" }
        }
    }
}
