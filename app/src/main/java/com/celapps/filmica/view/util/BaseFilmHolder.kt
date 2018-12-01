package com.celapps.filmica.view.util

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.celapps.filmica.data.Film

open class BaseFilmHolder(itemView: View,
                     clickListener: ((Film) -> Unit)? = null): RecyclerView.ViewHolder(itemView) {

    lateinit var film: Film

    init {
        itemView.setOnClickListener {
            Log.d("Patata", "Item cliqueado")
            clickListener?.invoke(film)
        }
    }

    open fun bindFilm(film: Film) {
        this.film = film
    }

}