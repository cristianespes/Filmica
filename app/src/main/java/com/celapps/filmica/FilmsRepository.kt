package com.celapps.filmica

// Singleton
object FilmsRepository { // Todo estará en un contexto estático
    val films: MutableList<Film> = mutableListOf()
    get() {
        if (field.isEmpty()) {
            field.addAll(dummyFilms())
        }

        return field
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
}