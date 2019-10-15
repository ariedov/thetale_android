package org.thetale.core

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

fun openUrl(context: Context, url: String) {
    val builder = CustomTabsIntent.Builder()
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
}