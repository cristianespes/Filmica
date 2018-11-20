package com.celapps.filmica

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_films.*

class FilmsFragment: Fragment() {

    lateinit var listener: OnItemClickListener

    val list: RecyclerView by lazy {
        val instance = view!!.findViewById<RecyclerView>(R.id.list_films) // la vista que estamos retornando en el onCreateView
        instance.addItemDecoration(ItemOffsetDecoration(R.dimen.offset_grid))
        instance.setHasFixedSize(true)

        instance // retorna la instancia
    }

    val adapter: FilmsAdapter by lazy {
        val instance = FilmsAdapter { film ->
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
        return inflater.inflate(R.layout.fragment_films, container, false)
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
        FilmsRepository.discoverFilms(context!!,
            {films ->
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
    }

    interface OnItemClickListener {
        fun onItemClicked(film: Film)
    }

}