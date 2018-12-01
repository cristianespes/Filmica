package com.celapps.filmica.view.watchlist


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_watchlist.*

import com.celapps.filmica.R
import com.celapps.filmica.data.Film
import com.celapps.filmica.data.FilmsRepository
import com.celapps.filmica.view.details.DetailsFragment
import com.celapps.filmica.view.util.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_films.*

class WatchlistFragment : Fragment() {

    lateinit var listener: OnItemClickListener

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

        FilmsRepository.watchlist(context!!) {films ->
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
        var film = adapter.getFilm(position)
        FilmsRepository.deleteFilm(context!!, film) {
            adapter.removeFilmAt(position)
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
