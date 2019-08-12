package com.celapps.filmica.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Html
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.celapps.filmica.R
import com.celapps.filmica.view.util.Utils
import java.text.Normalizer

class StringUtils {
    companion object {
        @JvmStatic
        val EMAIL_REGEX =
            "^(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])\$"
    }
}

fun String.isEmail() = StringUtils.EMAIL_REGEX.toRegex().matches(this)

fun String.normalize() = Normalizer.normalize(this, Normalizer.Form.NFD)
    .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")

fun String.toHtml() = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
} else {
    Html.fromHtml(this)
}

fun String.openUrlChromeCustomTabs(context: Context?) {
    context?.let {
        try {
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(ContextCompat.getColor(it, R.color.colorPrimary))
            builder.addDefaultShareMenuItem()
            builder.setShowTitle(true)
            builder.setStartAnimations(it, R.anim.slide_in_right, R.anim.slide_out_left)
            builder.setExitAnimations(it, R.anim.slide_in_left, R.anim.slide_out_right)
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(it, Uri.parse(Utils.checkUrlScheme(this)))
        } catch (e: Exception) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(this)
                it.startActivity(intent)
            } catch (e1: Exception) {
                Toast.makeText(it, R.string.error_opening_link, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

fun String.getOpenUrlPendingIntent(url: String): Intent {
    return Intent(Intent.ACTION_VIEW, Uri.parse(Utils.checkUrlScheme(url)))
}

fun String.isNumeric() : Boolean {
    var numeric = true

    try {
        val num = this.toDouble()
    } catch (e: NumberFormatException) {
        numeric = false
    }

    return numeric
}
