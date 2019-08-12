package com.celapps.filmica.view.films

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.VolleyError
import com.celapps.filmica.R
import com.celapps.filmica.data.ApiConstants
import com.celapps.filmica.data.Film
import com.celapps.filmica.data.FilmsRepository
import com.celapps.filmica.view.details.DetailsActivity
import com.celapps.filmica.view.details.DetailsFragment
import com.celapps.filmica.view.placeholder.PlaceholderFragment
import com.celapps.filmica.view.privacypolicy.PrivacyPolicyActivity
import com.celapps.filmica.view.search.SearchFragment
import com.celapps.filmica.view.trending.TrendingFragment
import com.celapps.filmica.view.watchlist.WatchlistFragment
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_films.*
import java.util.*

const val TAG_FILMS = "films"
const val TAG_WATCHLIST = "watchlist"
const val TAG_SEARCH = "search"
const val TAG_TRENDING = "trending"

class FilmsActivity: AppCompatActivity(), FilmsFragment.OnItemClickListener, WatchlistFragment.OnItemClickListener, TrendingFragment.OnItemClickListener, SearchFragment.OnItemClickListener, DetailsFragment.OnItemClickListener {

    private lateinit var filmsFragment: FilmsFragment
    private lateinit var watchlistFragment: WatchlistFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var trendingFragment: TrendingFragment
    private lateinit var activeFragment: Fragment

    //private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onItemClicked(film: Film) {
        this.showDetails(film.id, activeFragment.tag ?: TAG_FILMS)
    }

    override fun onPrivacyPolicyClicked() {
        launchPrivacyPolicyActivity()
    }

    override fun onButtonClicked(film: Film) {
        watchlistFragment.addFilm(film)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Stetho.initializeWithDefaults(this)

        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_films)

        if (savedInstanceState == null) {

            requestGenres(
                callbackSuccess = {
                    // Generar fragmentos
                    setupFragments() },
            callbackError = {error ->
                // Asignar géneros por defecto
                FilmsRepository.genres.putAll(ApiConstants.genres)
                setupFragments()
                //error.printStackTrace()
            })

        } else {
            requestGenres(
                callbackSuccess = {
                    val activeTag = savedInstanceState.getString("active", TAG_FILMS)
                    // Restaurar fragmentos
                    restoreFragments(activeTag) },
                callbackError = {error ->
                    // Asignar géneros por defecto
                    FilmsRepository.genres.putAll(ApiConstants.genres)
                    error.printStackTrace()
                })
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



    override fun onResume() {
        super.onResume()

        //firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        //firebaseAnalytics.setCurrentScreen(this, "Actividad de Films", null)
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

        if (isTablet()) showPlaceholderFragment()
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

    private fun showPlaceholderFragment() {
        val placeholder = PlaceholderFragment.newInstance()

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerDetails, placeholder)
            .commit()
    }


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

    private fun launchPrivacyPolicyActivity() {
        val intent = Intent(this, PrivacyPolicyActivity::class.java)
        startActivity(intent)
    }

    private fun requestGenres(callbackSuccess: () -> Unit,
                              callbackError: (VolleyError) -> Unit) {
        FilmsRepository.requestFilmGenres(
            language = Locale.getDefault().language,
            context = this,
            callbackSuccess = {
                callbackSuccess.invoke()
            },
            callbackError = {error ->
                callbackError.invoke(error)
            })
    }

}