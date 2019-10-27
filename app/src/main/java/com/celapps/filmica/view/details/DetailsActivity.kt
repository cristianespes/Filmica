package com.celapps.filmica.view.details

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.celapps.filmica.R
import com.celapps.filmica.data.Film
import com.celapps.filmica.view.imageviewer.ImageViewerActivity
import com.celapps.filmica.view.imageviewer.ImageViewerFragment
import com.google.firebase.analytics.FirebaseAnalytics

class DetailsActivity : AppCompatActivity(), DetailsFragment.OnItemClickListener {
    override fun onButtonClicked(film: Film) {}

    override fun onItemClicked(id: String, imageUrl: String) {
        launchImageViewerActivity(id, imageUrl, "ImageViewerActivity")
    }

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        if (savedInstanceState == null) {

            // ActionBar
            supportActionBar?.setDisplayHomeAsUpEnabled(true) // Back Button

            val id = intent.getStringExtra("id")
            val tag = intent.getStringExtra("tag")

            // TODO: Mostar error
            if (id == null || tag == null) return

            val detailsFragment =
                DetailsFragment.newInstance(id, tag) // Creamos fragmento

            supportFragmentManager.beginTransaction()
                .add(R.id.containerDetails, detailsFragment)
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

    private fun launchImageViewerActivity(id: String, imageUrl: String, tag: String) {
        val intent = Intent(this, ImageViewerActivity::class.java)
        intent.putExtra(ImageViewerFragment.PARAM_ID, id)
        intent.putExtra(ImageViewerFragment.PARAM_IMAGE_URL, imageUrl)
        intent.putExtra(ImageViewerFragment.PARAM_TAG, tag)
        startActivity(intent)
    }
}
