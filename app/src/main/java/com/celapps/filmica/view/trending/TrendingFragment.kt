package com.celapps.filmica.view.trending

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.celapps.filmica.R
import com.celapps.filmica.data.Film
import com.celapps.filmica.data.FilmsRepository
import com.celapps.filmica.view.util.EndlessScrollListener
import com.celapps.filmica.view.util.ItemOffsetDecoration
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.fragment_trending.*
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.layout_progress.*
import java.util.*

class TrendingFragment: Fragment() {

    private var LastLoadPage: Int = 1
    private var totalPages: Int? =  null

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    lateinit var listener: OnItemClickListener

    val list: RecyclerView by lazy {
        val instance = view!!.findViewById<RecyclerView>(R.id.list_films) // la vista que estamos retornando en el onCreateView
        instance.addItemDecoration(ItemOffsetDecoration(R.dimen.offset_grid))
        instance.setHasFixedSize(true)

        instance // retorna la instancia
    }

    val adapter: TrendingAdapter by lazy {
        val instance = TrendingAdapter { film ->
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trending, container, false)
    }

    // Se ejecuta siempre después de onCreateView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list.adapter = adapter

        list.addOnScrollListener(object : EndlessScrollListener(list.layoutManager!!) {
            override fun onLoadMore(currentPage: Int, totalItemCount: Int) {
                if (totalItemCount > 1) {
                    LastLoadPage++
                    totalPages?.let {
                        if (it > LastLoadPage) layoutProgress.visibility = View.VISIBLE
                        if (it >= LastLoadPage) reload(LastLoadPage)
                    }
                }
            }

            override fun onScroll(firstVisibleItem: Int, dy: Int, scrollPosition: Int) { }
        })

        btnRetry.setOnClickListener { this.reload() }

        this.reload()
    }

    override fun onResume() {
        super.onResume()

        firebaseAnalytics = FirebaseAnalytics.getInstance(context!!)
        firebaseAnalytics.setCurrentScreen(activity!!, "Fragmento de Trending", null)
    }

    fun reload(page: Int = 1) {
        FilmsRepository.trendingFilms(page = page,
            language = Locale.getDefault().language,
            context = context!!,
            callbackSuccess = {films, totalPages ->
                if (this.totalPages == null) this.totalPages = totalPages
                progress.visibility = View.INVISIBLE
                layoutError.visibility = View.INVISIBLE
                layoutProgress.visibility = View.INVISIBLE
                list.visibility = View.VISIBLE
                if (page == 1 ) adapter.setFilms(films) else adapter.updateFilms(films)
            },

            callbackError = {error ->
                progress.visibility = View.INVISIBLE
                layoutError.visibility = View.VISIBLE
                layoutProgress.visibility = View.INVISIBLE
                list.visibility = View.INVISIBLE

                error.printStackTrace()
            })
    }

    interface OnItemClickListener {
        fun onItemClicked(film: Film)
    }

}