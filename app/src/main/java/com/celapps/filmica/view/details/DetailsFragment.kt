package com.celapps.filmica.view.details

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.util.Log
import android.view.*
import android.widget.Toast
import com.celapps.filmica.R
import com.celapps.filmica.data.Film
import com.celapps.filmica.data.FilmsRepository
import com.celapps.filmica.view.films.FilmsActivity
import com.celapps.filmica.view.util.SimpleTarget
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment: Fragment() {

    lateinit var listener: OnItemClickListener

    // Método estático dentro de la clase
    companion object {
        fun newInstance(id: String, tag: String) : DetailsFragment {
            val instance = DetailsFragment()
            val args = Bundle()
            args.putString("id", id)
            args.putString("tag", tag)
            instance.arguments = args

            return instance
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnItemClickListener) {
            listener = context // Inicializar el listener
        }
    }

    private var film: Film? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true) // para avisar que vamos a crear un menú
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id: String = arguments?.getString("id") ?: ""
        val tag: String = arguments?.getString("tag") ?: ""

        film = FilmsRepository.findFilmById(id, tag)

        film?.let {
            with(it) {
                labelTitle.text = title // podemos utilizar el id indicado en el activity gracias al plugin android-extension
                labelOverview.text = overview
                labelGenre.text = genre
                labelRelease.text = release
                labelVotes.text = voteRating.toString()

                loadImage(it)
            }
        }

        btnAdd.setOnClickListener {
            film?.let {
                FilmsRepository.saveFilm(context!!, it) {film ->
                    Toast.makeText(context, getString(R.string.added_to_list), Toast.LENGTH_SHORT).show()
                    if (context is FilmsActivity) {
                        this.listener.onButtonClicked(film) // Solo en tablets
                    }

                    Snackbar
                        .make( fragment_details, getString(R.string.stored_movie), Snackbar.LENGTH_LONG )
                        .setAction(getString(R.string.undo)) {
                            context?.let {context ->
                                FilmsRepository.deleteFilm(context, film)
                                if (context is FilmsActivity) {
                                    this.listener.onButtonClicked(film) // Solo en tablets
                                }
                            }
                        }
                        .setActionTextColor(ContextCompat.getColor(context!!, R.color.colorSnackbarAction))
                        .show()
                }
            }
        }

        when(tag) {
            FilmsRepository.TAG_FILMS -> btnAdd.show()
            FilmsRepository.TAG_WATCHLIST -> btnAdd.hide()
            FilmsRepository.TAG_SEARCH -> btnAdd.show()
            FilmsRepository.TAG_TRENDING -> btnAdd.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Validar que es el item que esperamos
        //if (item?.itemId == R.id.action_share) {
            // Code
        //}

        item.takeIf { item?.itemId == R.id.action_share }?.let {menuItem ->
            shareFilm()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun shareFilm() {
        val intent = Intent(Intent.ACTION_SEND)

        // Validar que el film no sea nulo
        film?.let {
            val text = getString(R.string.template_share, it.title, it.voteRating)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, text)
        }

        startActivity(Intent.createChooser(intent, getString(R.string.title_share)))
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

    interface OnItemClickListener {
        fun onButtonClicked(film: Film)
    }
}
