package com.celapps.filmica.view.trending

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.celapps.filmica.R
import com.celapps.filmica.data.FilmsRepository
import com.celapps.filmica.view.util.ItemOffsetDecoration
import kotlinx.android.synthetic.main.fragment_trending.*
import kotlinx.android.synthetic.main.layout_error.*
import java.util.*

class TrendingFragment: Fragment() {

//    lateinit var listener: OnItemClickListener

    val list: RecyclerView by lazy {
        val instance = view!!.findViewById<RecyclerView>(R.id.list_films) // la vista que estamos retornando en el onCreateView
        instance.addItemDecoration(ItemOffsetDecoration(R.dimen.offset_grid))
        instance.setHasFixedSize(true)

        instance // retorna la instancia
    }

//    val adapter: TrendingAdapter by lazy {
//        val instance = TrendingAdapter { film ->
//            this.listener.onItemClicked(film)
//        }
//
//        instance
//    }

    val adapter: TrendingAdapter by lazy {
        val instance = TrendingAdapter()
        instance
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

//        if (context is OnItemClickListener) {
//            listener = context
//        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_trending, container, false)
    }

    // Se ejecuta siempre después de onCreateView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list.adapter = adapter
        btnRetry.setOnClickListener { this.reload() }
    }

    override fun onResume() {
        super.onResume()

        this.reload()
    }

    fun reload() {
        FilmsRepository.trendingFilms(context!!,
            {films, total_pages ->
                progress.visibility = View.INVISIBLE
                layoutError.visibility = View.INVISIBLE
                list.visibility = View.VISIBLE
                adapter.setFilms(films)
            },

            {error ->
                progress.visibility = View.INVISIBLE
                layoutError.visibility = View.VISIBLE
                list.visibility = View.INVISIBLE

                error.printStackTrace()
            })

        /*FilmsRepository.searchFilms(
            query = "ven",
            page = 1,
            language = Locale.getDefault().language,
            context = context!!,

            callbackSuccess = {films, total_pages ->
                progress.visibility = View.INVISIBLE
                layoutError.visibility = View.INVISIBLE
                list.visibility = View.VISIBLE
                adapter.setFilms(films)
            },

            callbackError = {error ->
                progress.visibility = View.INVISIBLE
                layoutError.visibility = View.VISIBLE
                list.visibility = View.INVISIBLE

                error.printStackTrace()
            })*/
    }

//    interface OnItemClickListener {
//        fun onItemClicked(film: Film)
//    }

}