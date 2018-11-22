package com.celapps.filmica.data

import android.content.Context
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

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

        if (films.isEmpty()) {
            requestDiscoverFilms(callbackSuccess, callbackError, context)
        } else {
            callbackSuccess.invoke(films)
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
                films.addAll(
                    Film.parseFilms(
                        response
                    )
                )
                callbackSuccess.invoke(films)
            }, {error ->
                callbackError.invoke(error)
            })

        Volley.newRequestQueue(context)
            .add(request)
    }

}