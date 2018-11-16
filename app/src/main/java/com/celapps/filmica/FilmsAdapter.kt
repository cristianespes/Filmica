package com.celapps.filmica

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

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
            // Lo asignamos también a la vista
            itemView.findViewById<TextView>(R.id.label_title).text = value?.title
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