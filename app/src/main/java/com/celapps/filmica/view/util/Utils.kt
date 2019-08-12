package com.celapps.filmica.view.util

object Utils {
    fun checkUrlScheme(url: String): String {
        return if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "http://$url"
        } else url

    }
}