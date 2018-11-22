package com.celapps.filmica.view.films

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.celapps.filmica.R
import com.celapps.filmica.data.Film
import com.celapps.filmica.view.details.DetailsActivity
import com.celapps.filmica.view.details.DetailsFragment
import kotlinx.android.synthetic.main.activity_films.*

class FilmsActivity: AppCompatActivity(), FilmsFragment.OnItemClickListener {
    override fun onItemClicked(film: Film) {
        this.showDetails(film.id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_films)

        if (savedInstanceState == null) {
            val filmsFragment = FilmsFragment() // Creamos fragmento

            supportFragmentManager.beginTransaction()
                .add(R.id.container_list, filmsFragment)
                .commit()
        }

    }

    fun showDetails(id: String) {
        if (isTablet())
            showDetailsFragment(id)
        else
            launchDetailsActivity(id)
    }

    private fun isTablet() = this.containerDetails != null


    private fun showDetailsFragment(id: String) {
        val detailsFragment = DetailsFragment.newInstance(id)

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerDetails, detailsFragment)
            .commit()
    }

    private fun launchDetailsActivity(id: String) {
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

}