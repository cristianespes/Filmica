package com.celapps.filmica.view.privacypolicy

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.celapps.filmica.R
import kotlinx.android.synthetic.main.activity_privacy_policy.*

class PrivacyPolicyActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /*webView_privacyPolicy.settings.builtInZoomControls = true
        webView_privacyPolicy.setInitialScale(1)
        webView_privacyPolicy.settings.javaScriptEnabled = true*/
        webView_privacyPolicy.settings.useWideViewPort = true
        webView_privacyPolicy.settings.javaScriptEnabled = true
        webView_privacyPolicy.settings.setAppCacheEnabled(false)
        webView_privacyPolicy.loadUrl("https://celdevelop.wixsite.com/filmica")
    }



    // Back View
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
