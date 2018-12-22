package com.celapps.filmica.view.details

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.celapps.filmica.R
import com.google.firebase.analytics.FirebaseAnalytics

class DetailsActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        if (savedInstanceState == null) {

            // ActionBar
            val actionBar = supportActionBar!!
            actionBar.setDisplayHomeAsUpEnabled(true) // Back Button

            val id = intent.getStringExtra("id")
            val tag = intent.getStringExtra("tag")

            val detailsFragment =
                DetailsFragment.newInstance(id, tag) // Creamos fragmento

            supportFragmentManager.beginTransaction()
                .add(R.id.container_details, detailsFragment)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.setCurrentScreen(this, "Actividad de Detalle", null)
    }

    // Back View
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
