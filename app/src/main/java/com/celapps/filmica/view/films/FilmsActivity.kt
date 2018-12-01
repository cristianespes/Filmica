package com.celapps.filmica.view.films

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.celapps.filmica.R
import com.celapps.filmica.data.Film
import com.celapps.filmica.view.details.DetailsActivity
import com.celapps.filmica.view.details.DetailsFragment
import com.celapps.filmica.view.search.SearchFragment
import com.celapps.filmica.view.trending.TrendingFragment
import com.celapps.filmica.view.watchlist.WatchlistFragment
import kotlinx.android.synthetic.main.activity_films.*

const val TAG_FILMS = "films"
const val TAG_WATCHLIST = "watchlist"
const val TAG_SEARCH = "search"
const val TAG_TRENDING = "trending"

class FilmsActivity: AppCompatActivity(), FilmsFragment.OnItemClickListener {

    private lateinit var filmsFragment: FilmsFragment
    private lateinit var watchlistFragment: WatchlistFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var trendingFragment: TrendingFragment
    private lateinit var activeFragment: Fragment

    override fun onItemClicked(film: Film) {
        this.showDetails(film.id, activeFragment.tag ?: TAG_FILMS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_films)

        if (savedInstanceState == null) {
            // Generar fragmentos
            setupFragments()
        } else {
            val activeTag = savedInstanceState.getString("active", TAG_FILMS)
            // Restaurar fragmentos
            restoreFragments(activeTag)
        }


        // Evento para la barra inferior del menú de navegación
        navigation?.setOnNavigationItemSelectedListener { item ->
            val id = item.itemId

            when (id) {
                R.id.action_discover -> showMainFragment(filmsFragment)
                R.id.action_watchlist -> showMainFragment(watchlistFragment)
                R.id.action_search -> showMainFragment(searchFragment)
                R.id.action_trending -> showMainFragment(trendingFragment)
            }
            true
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("active", activeFragment.tag)
    }

    private fun setupFragments() {
        // Creamos fragmentos
        filmsFragment = FilmsFragment()
        watchlistFragment = WatchlistFragment()
        searchFragment = SearchFragment()
        trendingFragment = TrendingFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.container_list, filmsFragment, TAG_FILMS)
            .add(R.id.container_list, watchlistFragment, TAG_WATCHLIST)
            .add(R.id.container_list, searchFragment, TAG_SEARCH)
            .add(R.id.container_list, trendingFragment, TAG_TRENDING)
            .hide(watchlistFragment)
            .hide(searchFragment)
            .hide(trendingFragment)
            .commit()

        activeFragment = filmsFragment
    }

    private fun restoreFragments(tag: String) {
        filmsFragment = supportFragmentManager.findFragmentByTag(TAG_FILMS) as FilmsFragment
        watchlistFragment = supportFragmentManager.findFragmentByTag(TAG_WATCHLIST) as WatchlistFragment
        searchFragment = supportFragmentManager.findFragmentByTag(TAG_SEARCH) as SearchFragment
        trendingFragment = supportFragmentManager.findFragmentByTag(TAG_TRENDING) as TrendingFragment


        when (tag) {
            TAG_WATCHLIST -> activeFragment = watchlistFragment
            TAG_SEARCH -> activeFragment = searchFragment
            TAG_TRENDING -> activeFragment = trendingFragment
            else -> activeFragment = filmsFragment
        }
    }

    private fun showMainFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(fragment)
            .commit()

        activeFragment = fragment
    }

    fun showDetails(id: String, tag: String) {
        if (isTablet())
            showDetailsFragment(id, tag)
        else
            launchDetailsActivity(id, tag)
    }

    private fun isTablet() = this.containerDetails != null


    private fun showDetailsFragment(id: String, tag: String) {
        val detailsFragment = DetailsFragment.newInstance(id, tag)

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerDetails, detailsFragment)
            .commit()
    }

    private fun launchDetailsActivity(id: String, tag: String) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("id", id)
        intent.putExtra("tag", tag)
        startActivity(intent)
    }

}