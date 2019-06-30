package com.celapps.filmica.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

@Entity
data class Film(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo (name = "vote_rating")
    var voteRating: Double = 0.0,
    var title: String = "",
    var genre: String = "",
    var release: String = "",
    var overview: String = "",
    var poster: String = "",
    var backdrop: String = ""
) {

    // Si intento ejecutar una creación sin argumento, ignorar esa insercción a la BBDD
    @Ignore
    constructor(): this("")

    fun getPosterUrl() : String = "$BASE_POSTER_URL$poster"

    fun getBackdropUrl() : String = "$BASE_POSTER_URL$backdrop"

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
                genre = if (jsonFilm.optJSONArray("genre_ids").length() > 0) parseGenres(jsonFilm.optJSONArray("genre_ids")) else "",
                backdrop = jsonFilm.optString("backdrop_path", "")
            )
        }

        private fun parseGenres(genresArray: JSONArray) : String {

            val genres = mutableListOf<String>()

            for ( i in 0..(genresArray.length() - 1) ) {
                val genreId = genresArray.getInt(i)
                val genre = FilmsRepository.genres[genreId] ?: ""
                genres.add(genre)
            }

            // acc: acumulador
            return genres.reduce { acc, genre -> "$acc | $genre" }
        }

    }
}

object Genre {
        fun parseGenresAPI(response: JSONObject) : MutableMap<Int, String> {
            val genresArray = response.getJSONArray("genres")
            var map: MutableMap<Int, String> = mutableMapOf()

            for (i in 0..(genresArray.length() - 1)) {
                val genrePair = parseGenre(genresArray.getJSONObject(i))
                map.put(genrePair.first, genrePair.second)
            }

            return map
        }

        private fun parseGenre(jsonGenre: JSONObject) : Pair<Int, String> {
            return Pair(jsonGenre.getInt("id"), jsonGenre.getString("name"))
        }
}

