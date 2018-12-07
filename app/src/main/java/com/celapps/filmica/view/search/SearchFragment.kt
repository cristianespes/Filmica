package com.celapps.filmica.view.search

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.view.*
import com.celapps.filmica.R
import com.celapps.filmica.data.Film
import com.celapps.filmica.data.FilmsRepository
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.layout_not_found.*
import java.util.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast


class SearchFragment: Fragment() {

    lateinit var listener: OnItemClickListener

    val adapter: SearchAdapter by lazy {
        val instance = SearchAdapter { film ->
            this.listener.onItemClicked(film)
        }

        instance
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Para indicar que en el ciclo de vida de este fragmento, va a ejecutar un callback con un menú

        this.reload()
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
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.action_search_toolbar)

        if (searchItem != null) {

            val searchView = searchItem.actionView as SearchView
            searchView.maxWidth = Integer.MAX_VALUE // Set search menu as full width
            // searchView.queryHint = "Buscar película..."
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText == null || newText.trim().isEmpty()) {
                        progress.visibility = View.VISIBLE
                        reload()

                        return true
                    }

                    return false
                }

            })

            val searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text) as EditText
            searchEditText.setOnEditorActionListener { newText, actionId, keyEvent ->
                newText?.let { text ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH && !text.text.trim().isEmpty() && (text.text.length > 2)) {
                        progress.visibility = View.VISIBLE
                        reload(query = (text.text.toString().toLowerCase()))
                    } else if (actionId == EditorInfo.IME_ACTION_SEARCH && (text.text.length < 3)) {
                        val toast = Toast.makeText(context , getString(R.string.min_characters), Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.TOP, 0, 300)
                        toast.show()
                    }
                }

                false
            }

        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchfilmslist.adapter = adapter
        btnRetry.setOnClickListener { this.reload() }
    }

    fun reload(query: String = "a", page: Int = 1) {
        FilmsRepository.searchFilms(
            query = query,
            page = page,
            language = Locale.getDefault().language,
            context = context!!,

            callbackSuccess = {films, total_pages ->
                progress.visibility = View.INVISIBLE
                layoutError.visibility = View.INVISIBLE
                layoutNotFound.visibility = View.INVISIBLE
                if (films.size == 0) layoutNotFound.visibility = View.VISIBLE else layoutNotFound.visibility = View.INVISIBLE
                searchfilmslist.visibility = View.VISIBLE

                adapter.setFilms(films) //films.sortedWith(compareBy { it.title }).toMutableList()
            },

            callbackError = {error ->
                progress.visibility = View.INVISIBLE
                layoutError.visibility = View.VISIBLE
                layoutNotFound.visibility = View.INVISIBLE
                searchfilmslist.visibility = View.INVISIBLE

                error.printStackTrace()
            })
    }

    interface OnItemClickListener {
        fun onItemClicked(film: Film)
    }
}
