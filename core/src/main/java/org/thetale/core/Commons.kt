package org.thetale.core

import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent

fun openUrl(context: Context, url: String) {
    val builder = CustomTabsIntent.Builder()
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
}