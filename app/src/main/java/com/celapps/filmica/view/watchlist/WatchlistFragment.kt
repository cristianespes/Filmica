package com.celapps.filmica.view.watchlist


import android.content.Context
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_watchlist.*
import com.celapps.filmica.R
import com.celapps.filmica.data.Film
import com.celapps.filmica.data.FilmsRepository
import com.celapps.filmica.view.util.SwipeToDeleteCallback
import com.google.firebase.analytics.FirebaseAnalytics

class WatchlistFragment : Fragment() {

    lateinit var listener: OnItemClickListener

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val adapter: WatchlistAdapter by lazy {
        val instance = WatchlistAdapter { film ->
            this.listener.onItemClicked(film)
        }

        instance
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnItemClickListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_watchlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSwipeHandler()
        watchlist.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        context?.let {
            firebaseAnalytics = FirebaseAnalytics.getInstance(it)
            firebaseAnalytics.setCurrentScreen(activity!!, "Fragmento de Watchlist", null)
        }

        FilmsRepository.watchlist(context!!) { films ->
            adapter.setFilms(films.toMutableList())
        }
    }

    private fun setupSwipeHandler() {
        val swipeHandler = object : SwipeToDeleteCallback() {
            override fun onSwiped(holder: RecyclerView.ViewHolder, direction: Int) {
                deleteFilmAt(holder.adapterPosition)
            }

        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(watchlist)
    }

    private fun deleteFilmAt(position: Int) {
        val film = adapter.getFilm(position)
        FilmsRepository.deleteFilm(context!!, film) {
            adapter.removeFilmAt(position)

            Snackbar
                .make(watchlist, getString(R.string.deleted_movie), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.undo)) {
                    FilmsRepository.saveFilm(context!!, film)
                    adapter.addFilm(film)
                }
                .setActionTextColor(ContextCompat.getColor(context!!, R.color.colorSnackbarAction))
                .show()
        }
    }

    fun addFilm(film: Film) {
        FilmsRepository.watchlist(context!!) { films ->
            adapter.setFilms(films.toMutableList())
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(film: Film)
    }

}
