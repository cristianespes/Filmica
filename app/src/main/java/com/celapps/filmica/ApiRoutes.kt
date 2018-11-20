package com.celapps.filmica

import android.net.Uri

object ApiRoutes {
    // Método para introducir la petición a la API con posibilidad de variar parámetros
    fun discoverUrl(language: String = "en-US",
                    sort: String = "popularity.desc",
                    page: Int = 1): String {

        return getUriBuilder()
            .appendPath("discover")
            .appendPath("movie")
            .appendQueryParameter("language", language)
            .appendQueryParameter("sort_by", sort)
            .appendQueryParameter("include_adult", "false")
            .appendQueryParameter("include_video", "false")
            .appendQueryParameter("page", page.toString())
            .build()
            .toString()
    }

    private fun getUriBuilder() =
        Uri.Builder()
            .scheme("https")
            .authority("api.themoviedb.org")
            .appendPath("3")
            .appendQueryParameter("api_key", BuildConfig.MovieDBApiKey)
}