package com.celapps.filmica.view.details

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.celapps.filmica.R
import com.celapps.filmica.data.Film
import com.celapps.filmica.data.FilmsRepository
import com.celapps.filmica.view.util.SimpleTarget
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.item_film.view.*

class DetailsFragment: Fragment() {

    // Método estático dentro de la clase
    companion object {
        fun newInstance(id: String) : DetailsFragment {
            val instance = DetailsFragment()
            val args = Bundle()
            args.putString("id", id)
            instance.arguments = args

            return instance
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id: String = arguments?.getString("id") ?: ""
        val film = FilmsRepository.findFilmById(id)

        film?.let {
            with(film) {
                labelTitle.text = title // podemos utilizar el id indicado en el activity gracias al plugin android-extension
                labelOverview.text = overview
                labelGenre.text = genre
                labelRelease.text = release

                loadImage(film)
            }
        }

        btnAdd.setOnClickListener {
            Toast.makeText(context, "Added to list", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadImage(film: Film) {
        val target = SimpleTarget(
            successCallback = { bitmap, from ->
                // Adjuntamos la imagen
                imgPoster.setImageBitmap(bitmap) // Seteamos el método setImageBitmap en FadeImageView

                // Añadir el color el bitmap al contenedor
                setColorFrom(bitmap)
            }
        )

        // Strong Reference para que no destruya la instancia del target
        imgPoster.tag = target

        Picasso.get()
            .load(film?.getPosterUrl())
            .error(R.drawable.placeholder)
            .into(target)
    }

    private fun setColorFrom(bitmap: Bitmap) {
        Palette.from(bitmap).generate { palette ->
            val defaultColor = ContextCompat.getColor(context!!, R.color.colorPrimary) // Color por defecto si no extrae ninguno
            val swatch = palette?.vibrantSwatch ?: palette?.dominantSwatch // vibrantSwatch no siempre obtiene resultado, domiantSwatch suele obtener más
            val color = swatch?.rgb ?: defaultColor // tomamos el rgb generado por el swatch o en su defecto el que indicamos por defecto
            val overlayColor = Color.argb(
                (Color.alpha(color) * 0.5).toInt(),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
            )

            overlay.setBackgroundColor(overlayColor) // En la View encima de la imagen

            // Los colores en Appcompat en los botones funciona con tint
            btnAdd.backgroundTintList = ColorStateList.valueOf(color) // Color en el botón flotante
        }
    }
}