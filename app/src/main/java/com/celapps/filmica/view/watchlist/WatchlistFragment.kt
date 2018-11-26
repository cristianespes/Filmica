package com.celapps.filmica.view.watchlist


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_watchlist.*

import com.celapps.filmica.R
import com.celapps.filmica.data.FilmsRepository

class WatchlistFragment : Fragment() {

    val adapter: WatchlistAdapter by lazy {
        val instance = WatchlistAdapter()
        instance
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

        watchlist.adapter = adapter
    }

    override fun onResume() {
        super.onResume()

        FilmsRepository.watchlist(context!!) {films ->
            adapter.setFilms(films.toMutableList())
        }
    }

}
