package com.celapps.filmica.view.films

import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.celapps.filmica.R
import com.celapps.filmica.data.Film
import com.celapps.filmica.view.util.SimpleTarget
import com.squareup.picasso.Picasso
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

                    loadImage()
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

        private fun loadImage() {
            val target = SimpleTarget(
                successCallback = { bitmap, from ->
                    // Adjuntamos la imagen
                    itemView.imgPoster.setImageBitmap(bitmap) // Seteamos el método setImageBitmap en FadeImageView

                    // Añadir el color el bitmap al contenedor
                    setColorFrom(bitmap)
                }
            )

            // Strong Reference para que no destruya la instancia del target
            itemView.imgPoster.tag = target

            Picasso.get()
                .load(film?.getPosterUrl())
                .error(R.drawable.placeholder)
                .into(target)
        }

        private fun setColorFrom(bitmap: Bitmap) {
            Palette.from(bitmap).generate { palette ->
                val defaultColor = ContextCompat.getColor(itemView.context, R.color.colorPrimary) // Color por defecto si no extrae ninguno
                val swatch = palette?.vibrantSwatch ?: palette?.dominantSwatch // vibrantSwatch no siempre obtiene resultado, domiantSwatch suele obtener más
                val color = swatch?.rgb ?: defaultColor // tomamos el rgb generado por el swatch o en su defecto el que indicamos por defecto

                itemView.container.setBackgroundColor(color)
                itemView.containerData.setBackgroundColor(color)
            }
        }
    }

}