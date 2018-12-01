package com.celapps.filmica.view.details

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.celapps.filmica.R

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        if (savedInstanceState == null) {

            // ActionBar
            val actionBar = supportActionBar!!
            actionBar.setDisplayHomeAsUpEnabled(true) // Back Button

            val id = intent.getStringExtra("id")

            val detailsFragment =
                DetailsFragment.newInstance(id) // Creamos fragmento

            supportFragmentManager.beginTransaction()
                .add(R.id.container_details, detailsFragment)
                .commit()
        }
    }

    // Back View
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
