package com.celapps.filmica.view.util

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.celapps.filmica.data.Film

open class BaseFilmAdapter<VH: BaseFilmHolder>(
    @LayoutRes val layoutItem: Int,
    val holderCreator: ((View) -> VH)
): RecyclerView.Adapter<VH>() {

    protected val list: MutableList<Film> = mutableListOf()

    // 1 - Cuántos elementos se van a mostrar en la pantalla
    override fun getItemCount(): Int {
        return list.count()
    }

    // 2 - Genera la vista de las filas => viewHolder (Plantilla)
    override fun onCreateViewHolder(recyclerView: ViewGroup, viewType: Int): VH {
        // Inflamos fichero desde xml
        val itemView = LayoutInflater.from(recyclerView.context)
            .inflate(layoutItem, recyclerView, false)

        // Le pasamos este item al ViewHolder
        return holderCreator.invoke(itemView)// Instancia de una clase genérica
    }

    // 3 - Acceso a los datos de la colección para setearlos en el elemento del ViewHolder
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

    fun updateFilms(films: MutableList<Film>) {
        list.addAll(films)
        notifyItemRangeInserted(list.size, films.size)
    }

    fun getFilm(position: Int) : Film {
        return list.get(position)
    }

    fun removeFilmAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addFilm(film: Film, position: Int? = null) {
        if (position == null) list.add(film) //else list.add(position, film)
        notifyItemInserted(list.size)
    }
}
