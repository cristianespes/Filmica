package com.celapps.filmica.view.search

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import android.text.Editable
import android.text.TextWatcher
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
import com.google.firebase.analytics.FirebaseAnalytics


class SearchFragment : Fragment() {

    lateinit var listener: OnItemClickListener

    private lateinit var firebaseAnalytics: FirebaseAnalytics

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
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

            val searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText
            searchEditText.setOnEditorActionListener { newText, actionId, keyEvent ->
                newText?.let { text ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH && (text.text.length < 3)) {
                        val toast = Toast.makeText(context, getString(R.string.min_characters), Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.TOP, 0, 300)
                        toast.show()
                    } /*else if (actionId == EditorInfo.IME_ACTION_SEARCH && !text.text.trim().isEmpty() && (text.text.length > 2)) {
                        progress.visibility = View.VISIBLE
                        reload(query = (text.text.toString().toLowerCase()))
                    }*/
                }

                false
            }

            searchEditText.addTextChangedListener(object : TextWatcher {
                var timer = Timer()

                override fun afterTextChanged(p0: Editable?) {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            activity!!.runOnUiThread {
                                if (!searchEditText.text.trim().isEmpty() && (searchEditText.text.length > 2)) {
                                    progress.visibility = View.VISIBLE
                                    reload(query = (searchEditText.text.toString().toLowerCase()))
                                }
                            }
                        }
                    }, 1000)
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    timer.cancel()
                }

            })

        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchfilmslist.adapter = adapter
        btnRetry.setOnClickListener { this.reload() }
    }

    override fun onResume() {
        super.onResume()

        context?.let {
            firebaseAnalytics = FirebaseAnalytics.getInstance(it)
            firebaseAnalytics.setCurrentScreen(activity!!, "Fragmento de búsqueda", null)
        }
    }

    fun reload(query: String = "a", page: Int = 1) {

        if (query != "a") {
            val params = Bundle()
            params.putString("query", query)
            params.putString("page", page.toString())
            //firebaseAnalytics.logEvent("query_film", params)
        }

        FilmsRepository.searchFilms(
            query = query,
            page = page,
            language = Locale.getDefault().language,
            context = context!!,

            callbackSuccess = { films, total_pages ->
                progress.visibility = View.INVISIBLE
                layoutError.visibility = View.INVISIBLE
                if (films.size == 0) {
                    layoutNotFound.visibility = View.VISIBLE
                    searchfilmslist.visibility = View.INVISIBLE
                } else {
                    layoutNotFound.visibility = View.INVISIBLE
                    searchfilmslist.visibility = View.VISIBLE
                }


                adapter.setFilms(films) //films.sortedWith(compareBy { it.title }).toMutableList()
            },

            callbackError = { error ->
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
