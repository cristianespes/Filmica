package com.celapps.filmica.view.util

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.celapps.filmica.data.Film

open class BaseFilmAdapter<VH: BaseFilmHolder>(
    @LayoutRes val layoutItem: Int,
    val holderCreator: ((View) -> VH)
): RecyclerView.Adapter<VH>() {

    protected val list: MutableList<Film> = mutableListOf()

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onCreateViewHolder(recyclerView: ViewGroup, viewType: Int): VH {
        // Inflamos fichero desde xml
        val itemView = LayoutInflater.from(recyclerView.context)
            .inflate(layoutItem, recyclerView, false)

        // Le pasamos este item al ViewHolder
        return holderCreator.invoke(itemView)// Instancia de una clase genérica
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        // Obtenemos la película según la posición que se solicite rellenar
        val film = list[position]

        // Asignamos al VH el film
        viewHolder.bindFilm(film)
    }


    fun setFilms(films: MutableList<Film>) {
        list.clear()
        list.addAll(films)
        notifyDataSetChanged()
    }


}