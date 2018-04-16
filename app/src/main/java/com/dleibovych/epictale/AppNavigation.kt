package com.dleibovych.epictale

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import com.dleibovych.epictale.game.MainActivity
import org.thetale.core.AppNavigation

class TheTaleAppNavigation: AppNavigation {

    override fun openApp(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(context, intent, null)
    }
}