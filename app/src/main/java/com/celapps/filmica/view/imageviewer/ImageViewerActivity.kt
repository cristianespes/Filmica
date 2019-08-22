package com.celapps.filmica.view.imageviewer

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.celapps.filmica.R

class ImageViewerActivity : AppCompatActivity(), ImageViewerFragment.OnBackClickListener {
    override fun onBackClicked(id: String) {
        onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        if (savedInstanceState == null) {

            // ActionBar
            /*supportActionBar?.setDisplayShowTitleEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                supportActionBar?.elevation = 0.toFloat()
            }*/

            val id = intent.getStringExtra(ImageViewerFragment.PARAM_ID)
            val imageUrl = intent.getStringExtra(ImageViewerFragment.PARAM_IMAGE_URL)
            val tag = intent.getStringExtra(ImageViewerFragment.PARAM_TAG)

            // TODO: Mostar error
            if (id == null || imageUrl == null || tag == null) return

            val imageViewerFragment = ImageViewerFragment.newInstance(id, imageUrl, tag)

            supportFragmentManager.beginTransaction()
                .add(R.id.container_image_viewer, imageViewerFragment)
                .commit()
        }
    }

    // Back View
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
