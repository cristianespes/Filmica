package com.celapps.filmica.data

import android.arch.persistence.room.Room
import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

// Singleton
object FilmsRepository { // Todo estará en un contexto estático
    private val films: MutableList<Film> = mutableListOf()
    private val trendingFilms: MutableList<Film> = mutableListOf()
    private val searchFilms: MutableList<Film> = mutableListOf()

    @Volatile // Para que se pueda ejecuta en otro thread
    private var db: AppDatabase? = null
    //databaseBuilder solo debe ser ejecutado una vez, sino generará problemas

    private fun getDbInstance(context: Context) : AppDatabase {
        if (db == null) {
            db = Room.databaseBuilder(context, AppDatabase::class.java, "filmica-db").build()
        }

        return db as AppDatabase
    }

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


    // Método para realizar la petición GET del discover a la API
    fun discoverFilms(page: Int,
                      language: String,
                      context: Context,
                      callbackSuccess: ((MutableList<Film>) -> Unit),
                      callbackError: ((VolleyError) -> Unit)) {

        if (films.isEmpty()) {
            requestDiscoverFilms(page, language, callbackSuccess, callbackError, context)
        } else {
            callbackSuccess.invoke(films)
        }
    }

    // Método para realizar la petición GET del Trending a la API
    fun trendingFilms(context: Context,
                      callbackSuccess: ((MutableList<Film>) -> Unit),
                      callbackError: ((VolleyError) -> Unit)) {

        if (trendingFilms.isEmpty()) {
            requestTrendingFilms(callbackSuccess, callbackError, context)
        } else {
            callbackSuccess.invoke(trendingFilms)
        }
    }

    // Método para realizar la petición GET del Trending a la API
    fun searchFilms(query: String,
                    context: Context,
                      callbackSuccess: ((MutableList<Film>) -> Unit),
                      callbackError: ((VolleyError) -> Unit)) {

        if (searchFilms.isEmpty()) {
            requestSearchFilms(query ,callbackSuccess, callbackError, context)
        } else {
            callbackSuccess.invoke(searchFilms)
        }
    }

    fun saveFilm(context: Context, film: Film, callbackSuccess: ((Film) -> Unit)) {

        GlobalScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().insertFilm(film)
            }

            async.await()
            callbackSuccess.invoke(film)
        }


    }

    fun watchlist(context: Context, callbackSuccess: ((List<Film>) -> Unit)) {
        GlobalScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().getFilms()
            }

            val films: List<Film> = async.await()
            callbackSuccess.invoke(films)
        }
    }

    fun deleteFilm(context: Context, film: Film, callbackSuccess: ((Film) -> Unit)) {
        GlobalScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().deleteFilm(film)
            }
            async.await()
            callbackSuccess.invoke(film)
        }
    }

    private fun requestDiscoverFilms(
        page: Int,
        language: String,
        callbackSuccess: (MutableList<Film>) -> Unit,
        callbackError: (VolleyError) -> Unit,
        context: Context
    ) {
        val url = ApiRoutes.discoverUrl(page = page, language = language)

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

    private fun requestTrendingFilms(
        callbackSuccess: (MutableList<Film>) -> Unit,
        callbackError: (VolleyError) -> Unit,
        context: Context
    ) {
        val url = ApiRoutes.trendingUrl()

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            {response ->
                trendingFilms.addAll(
                    Film.parseFilms(
                        response
                    )
                )
                callbackSuccess.invoke(trendingFilms)
            }, {error ->
                callbackError.invoke(error)
            })

        Volley.newRequestQueue(context)
            .add(request)
    }

    private fun requestSearchFilms(
        query: String,
        callbackSuccess: (MutableList<Film>) -> Unit,
        callbackError: (VolleyError) -> Unit,
        context: Context
    ) {
        val url = ApiRoutes.searchUrl(query)

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            {response ->
                searchFilms.addAll(
                    Film.parseFilms(
                        response
                    )
                )
                callbackSuccess.invoke(searchFilms)
            }, {error ->
                callbackError.invoke(error)
            })

        Volley.newRequestQueue(context)
            .add(request)
    }

}