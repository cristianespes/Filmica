package com.celapps.filmica.view.films

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.celapps.filmica.R
import com.celapps.filmica.data.Film
import kotlinx.android.synthetic.main.item_film.view.*

class FilmsAdapter(var itemClickListener: ((Film) -> Unit)? = null): RecyclerView.Adapter<FilmsAdapter.FilmViewHolder>() {

    private val list = mutableListOf<Film>() // Lista vacía

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onCreateViewHolder(recyclerView: ViewGroup, type: Int): FilmViewHolder {
        // Inflamos fichero desde xml
        val itemView = LayoutInflater.from(recyclerView.context)
                        .inflate(R.layout.item_film, recyclerView, false)

        // Le pasamos este item a al ViewHolder
        return FilmViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: FilmViewHolder, position: Int) {
        // Obtenemos la película según la posición que se solicite rellenar
        val film: Film = list[position]

        viewHolder.film = film
    }

    fun setFilms(films: MutableList<Film>) {
        list.clear()
        list.addAll(films)
        notifyDataSetChanged()
    }

    // Clase anidada
    inner class FilmViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var film: Film? = null
        set(value) {
            // Actualizamos la película
            field = value

            value?.let {
                // Lo asignamos también a la vista
                with(itemView) {
                    labelTitle.text = value.title
                    titleGenre.text = value.genre
                    labelVotes.text = value.voteRating.toString()
                }
            }
        }

        init {
            this.itemView.setOnClickListener {
                film?.let {
                    itemClickListener?.invoke(it)
                }
            }
        }
    }

}