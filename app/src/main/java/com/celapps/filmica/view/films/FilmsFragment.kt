package com.celapps.filmica.view.films

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.celapps.filmica.R
import com.celapps.filmica.data.Film
import com.celapps.filmica.data.FilmsRepository
import com.celapps.filmica.view.privacypolicy.PrivacyPolicyActivity
import com.celapps.filmica.view.util.EndlessScrollListener
import com.celapps.filmica.view.util.ItemOffsetDecoration
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.fragment_films.*
import kotlinx.android.synthetic.main.layout_error.*
import kotlinx.android.synthetic.main.layout_progress.*
import java.util.*

class FilmsFragment: Fragment() {

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

    val adapter: FilmsAdapter by lazy {
        val instance = FilmsAdapter { film ->
            this.listener.onItemClicked(film)
        }

        instance
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_privacy_policy, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.action_policy_privacy -> {
                listener.onPrivacyPolicyClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()

        context?.let {
            firebaseAnalytics = FirebaseAnalytics.getInstance(it)
            firebaseAnalytics.setCurrentScreen(activity!!, "Fragmento de Films", null)
        }
    }

    fun reload(page: Int = 1) {
        FilmsRepository.discoverFilms(page = page,
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
        fun onPrivacyPolicyClicked()
    }

}