package com.celapps.filmica

import org.json.JSONArray
import org.json.JSONObject
import java.util.*

data class Film(val id: String = UUID.randomUUID().toString(),
                var title: String = "",
                var genre: String = "",
                var release: String = "",
                var voteRating: Double = 0.0,
                var overview: String = "") {
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
                title = jsonFilm.getString("title"),
                overview = jsonFilm.getString("overview"),
                voteRating = jsonFilm.getDouble("vote_average"),
                release = jsonFilm.getString("release_date"),
                genre = parseGenres(jsonFilm.getJSONArray("genre_ids"))
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
