package com.celapps.filmica

import android.content.Context
import android.net.Uri
import android.support.v4.os.ConfigurationCompat
import android.util.Log
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

// Singleton
object FilmsRepository { // Todo estará en un contexto estático
    private val films: MutableList<Film> = mutableListOf()

    fun findFilmById(id: String): Film? {
        return films.find { film -> film.id == id }
    }

    private fun dummyFilms(): List<Film> {
        return (0..9).map {
            Film(
                    title = "Film $it",
                    genre = "Genre $it",
                    release = "200$it-0$it-0$it",
                    voteRating = it.toDouble(),
                    overview = "Overview $it"
                )
        }
    }


    // Método para realizar la petición GET a la API
    fun discoverFilms(context: Context,
                      callbackSuccess: ((MutableList<Film>) -> Unit),
                      callbackError: ((VolleyError) -> Unit)) {

        if (this.films.isEmpty()) {
            requestDiscoverFilms(callbackSuccess, callbackError, context)
        } else {
            callbackSuccess.invoke(this.films)
        }
    }

    private fun requestDiscoverFilms(
        callbackSuccess: (MutableList<Film>) -> Unit,
        callbackError: (VolleyError) -> Unit,
        context: Context
    ) {
        val url = ApiRoutes.discoverUrl()

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            {response ->
                this.films.addAll(Film.parseFilms(response))
                callbackSuccess.invoke(this.films)
            }, {error ->
                callbackError.invoke(error)
            })

        Volley.newRequestQueue(context)
            .add(request)
    }

}