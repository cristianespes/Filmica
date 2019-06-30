package com.celapps.filmica.data

import androidx.room.Room
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
import kotlin.math.min

// Singleton
object FilmsRepository { // Todo está en un contexto estático
    val genres: MutableMap<Int, String> = mutableMapOf()
    private val films: MutableList<Film> = mutableListOf()
    private var totalPagesFilms: Int = 0
    private var watchlistFilms: MutableList<Film> = mutableListOf()
    private val trendingFilms: MutableList<Film> = mutableListOf()
    private var totalPagesTrendingFilms: Int = 0
    private val searchFilms: MutableList<Film> = mutableListOf()
    private var totalPagesSearchFilms: Int = 0

    const val TAG_FILMS = "films"
    const val TAG_WATCHLIST = "watchlist"
    const val TAG_SEARCH = "search"
    const val TAG_TRENDING = "trending"

    @Volatile // Para que se pueda ejecuta en otro thread
    private var db: AppDatabase? = null
    //databaseBuilder solo debe ser ejecutado una vez, sino generará problemas

    private fun getDbInstance(context: Context) : AppDatabase {
        if (db == null) {
            db = Room
                .databaseBuilder(context, AppDatabase::class.java, "filmica-db")
                .addMigrations(MIGRATION_1_2)
                .build()
        }

        return db as AppDatabase
    }

    fun findFilmById(id: String, tag: String): Film? {
        when(tag) {
            TAG_FILMS -> return films.find { film -> film.id == id }
            TAG_WATCHLIST -> return watchlistFilms.find { film -> film.id == id }
            TAG_SEARCH -> return searchFilms.find { film -> film.id == id }
            TAG_TRENDING ->  return trendingFilms.find { film -> film.id == id }
        }

        return null
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
                      callbackSuccess: ((MutableList<Film>, Int) -> Unit),
                      callbackError: ((VolleyError) -> Unit)) {

        if (films.isEmpty()) {
            requestDiscoverFilms(page, language, callbackSuccess, callbackError, context)
        } else if (page > 1) {
            requestDiscoverFilms(page, language, callbackSuccess, callbackError, context)
        } else {
            callbackSuccess.invoke(films, totalPagesFilms)
        }
    }

    // Método para realizar la petición GET del Trending a la API
    fun trendingFilms(page: Int,
                      language: String,
                      context: Context,
                      callbackSuccess: ((MutableList<Film>, Int) -> Unit),
                      callbackError: ((VolleyError) -> Unit)) {

        if (trendingFilms.isEmpty()) {
            requestTrendingFilms(page, language, callbackSuccess, callbackError, context)
        } else if (page > 1) {
            requestTrendingFilms(page, language, callbackSuccess, callbackError, context)
        } else {
            callbackSuccess.invoke(trendingFilms, totalPagesTrendingFilms)
        }
    }

    // Método para realizar la petición GET del Trending a la API
    fun searchFilms(query: String,
                    page: Int,
                    language: String,
                    context: Context,
                      callbackSuccess: ((MutableList<Film>, Int) -> Unit),
                      callbackError: ((VolleyError) -> Unit)) {

        searchFilms.removeAll { true }
        requestSearchFilms(query, page, language, callbackSuccess, callbackError, context)
    }

    fun saveFilm(context: Context, film: Film, callbackSuccess: ((Film) -> Unit)? = null) {
        GlobalScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().insertFilm(film)
            }

            async.await()
            callbackSuccess?.invoke(film)
        }
    }

    fun watchlist(context: Context, callbackSuccess: ((List<Film>) -> Unit)) {
        GlobalScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().getFilms()
            }

            val films: List<Film> = async.await()

            // Almacenar Backdrop si no tiene...
            films.forEach { film ->
                if ( film.backdrop.isEmpty() ) {
                    requestSearchFilms(film.title, 1, "", callbackSuccess = { list, _ ->
                        list.forEach {searchFilm ->
                            if ( film.id == searchFilm.id ) {
                                film.backdrop = searchFilm.backdrop
                                updateFilm(context, film)
                            }
                        }
                    }, callbackError = {}, context = context)
                }
            }

            watchlistFilms = films.toMutableList()
            callbackSuccess.invoke(films)
        }
    }

    fun updateFilm(context: Context, film: Film, callbackSuccess: (() -> Unit)? = null) {
        GlobalScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().updateFilm(film)
            }

            async.await()
            callbackSuccess?.invoke()
        }
    }

    fun deleteFilm(context: Context, film: Film, callbackSuccess: ((Film) -> Unit)? = null) {
        GlobalScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                val db = getDbInstance(context)
                db.filmDao().deleteFilm(film)
            }
            async.await()
            callbackSuccess?.invoke(film)
        }
    }

    private fun requestDiscoverFilms(
        page: Int,
        language: String,
        callbackSuccess: (MutableList<Film>, Int) -> Unit,
        callbackError: (VolleyError) -> Unit,
        context: Context
    ) {
        val url = ApiRoutes.discoverUrl(page = page, language = language)

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            {response ->
                val newFilms: MutableList<Film> = mutableListOf()
                newFilms.addAll(
                    Film.parseFilms(
                        response
                    )
                )
                films.addAll(newFilms)
                totalPagesFilms = response.optInt("total_pages", 0)
                callbackSuccess.invoke(newFilms, totalPagesFilms)
            }, {error ->
                callbackError.invoke(error)
            })

        Volley.newRequestQueue(context)
            .add(request)
    }

    private fun requestTrendingFilms(
        page: Int,
        language: String,
        callbackSuccess: (MutableList<Film>, Int) -> Unit,
        callbackError: (VolleyError) -> Unit,
        context: Context
    ) {
        val url = ApiRoutes.trendingUrl(page = page, language = language)

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            {response ->
                val newTrendingFilms: MutableList<Film> = mutableListOf()
                newTrendingFilms.addAll(
                    Film.parseFilms(
                        response
                    )
                )
                trendingFilms.addAll(newTrendingFilms)
                totalPagesTrendingFilms = response.optInt("total_pages", 0)
                callbackSuccess.invoke(newTrendingFilms, totalPagesTrendingFilms)
            }, {error ->
                callbackError.invoke(error)
            })

        Volley.newRequestQueue(context)
            .add(request)
    }

    private fun requestSearchFilms(
        query: String,
        page: Int,
        language: String,
        callbackSuccess: (MutableList<Film>, Int) -> Unit,
        callbackError: (VolleyError) -> Unit,
        context: Context
    ) {
        val url = ApiRoutes.searchUrl(query, language,  page)

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            {response ->
                val auxList = Film.parseFilms( response )
                searchFilms.addAll( auxList.subList(0, min(10, auxList.size )) )
                // searchFilms.addAll( auxList )
                totalPagesSearchFilms = response.optInt("total_pages", 0)
                callbackSuccess.invoke(searchFilms, totalPagesSearchFilms)
            }, {error ->
                callbackError.invoke(error)
            })

        Volley.newRequestQueue(context)
            .add(request)
    }

    fun requestFilmGenres(language: String,
                                  context: Context,
                                  callbackSuccess: () -> Unit,
                                  callbackError: (VolleyError) -> Unit) {
        val url = ApiRoutes.genresUrl(language)

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            {response ->
                genres.putAll( Genre.parseGenresAPI( response ) )
                callbackSuccess.invoke()
            }, { error ->
                callbackError.invoke(error)
            })

        Volley.newRequestQueue(context)
            .add(request)
    }

}