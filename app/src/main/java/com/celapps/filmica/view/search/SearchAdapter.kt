package com.celapps.filmica.view.search
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import android.view.View
import com.celapps.filmica.R
import com.celapps.filmica.data.Film
import com.celapps.filmica.view.util.BaseFilmAdapter
import com.celapps.filmica.view.util.BaseFilmHolder
import com.celapps.filmica.view.util.SimpleTarget
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_search.view.*

class SearchAdapter(itemClickListener: ((Film) -> Unit)? = null): BaseFilmAdapter<SearchAdapter.SearchHolder>(
    layoutItem = R.layout.item_search,
    holderCreator = {view -> SearchHolder(view, itemClickListener) }) {

    class SearchHolder(itemView: View,
                       listener: ((Film) -> Unit)? = null) : BaseFilmHolder(itemView, listener) {

        override fun bindFilm(film: Film) {
            super.bindFilm(film)

            with(itemView) {
                labelTitle.text = film.title
                labelOverview.text = film.overview

                if (film.voteRating == 0.toDouble()) {
                    labelVotes.visibility = View.GONE
                } else {
                    labelVotes.text = film.voteRating.toString()
                    labelVotes.visibility = View.VISIBLE
                }

                loadImage()
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
                .placeholder(R.drawable.film_placeholder)
                .error(R.drawable.film_placeholder)
                .into(target)
        }

        private fun setColorFrom(bitmap: Bitmap) {
            Palette.from(bitmap).generate { palette ->
                val defaultColor = ContextCompat.getColor(
                    itemView.context,
                    R.color.colorPrimary
                ) // Color por defecto si no extrae ninguno
                val swatch = palette?.vibrantSwatch
                    ?: palette?.dominantSwatch // vibrantSwatch no siempre obtiene resultado, domiantSwatch suele obtener más
                val color = swatch?.rgb
                    ?: defaultColor // tomamos el rgb generado por el swatch o en su defecto el que indicamos por defecto
                val overlayColor = Color.argb(
                    (Color.alpha(color) * 0.5).toInt(),
                    Color.red(color),
                    Color.green(color),
                    Color.blue(color)
                )

                itemView.imgOverlay.setBackgroundColor(overlayColor)
            }
        }

    }

}