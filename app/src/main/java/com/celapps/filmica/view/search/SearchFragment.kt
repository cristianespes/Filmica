package com.celapps.filmica.view.search

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.celapps.filmica.R
import com.celapps.filmica.data.FilmsRepository
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.layout_error.*
import java.util.*

class SearchFragment: Fragment() {

    //    lateinit var listener: OnItemClickListener

    val adapter: SearchAdapter by lazy {
        val instance = SearchAdapter()
        instance
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchfilmslist.adapter = adapter
        btnRetry.setOnClickListener { this.reload() }
    }

    override fun onResume() {
        super.onResume()

        this.reload()
    }

    fun reload(page: Int = 1) {
        FilmsRepository.searchFilms(
            query = "ven",
            page = page,
            language = Locale.getDefault().language,
            context = context!!,

            callbackSuccess = {films, total_pages ->
                progress.visibility = View.INVISIBLE
                layoutError.visibility = View.INVISIBLE
                searchfilmslist.visibility = View.VISIBLE
                adapter.setFilms(films)
            },

            callbackError = {error ->
                progress.visibility = View.INVISIBLE
                layoutError.visibility = View.VISIBLE
                searchfilmslist.visibility = View.INVISIBLE

                error.printStackTrace()
            })
    }

}