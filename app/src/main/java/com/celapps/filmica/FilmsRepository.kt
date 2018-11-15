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

//        val films: MutableList<Film> = mutableListOf()
//        for (i in 1..9) {
//            films.add(
//                Film(
//                    title = "Film ${i}",
//                    genre = "Genre ${i}",
//                    release = "200${i}-0${i}-0${i}",
//                    voteRating = i.toDouble(),
//                    overview = "Overview ${i}"
//                )
//            )
//        }
//        return films

    }
}