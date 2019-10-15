package com.dleibovych.epictale

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.dleibovych.epictale.game.MainActivity
import org.thetale.core.AppNavigation

class TheTaleAppNavigation: AppNavigation {

    override fun openApp(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(context, intent, null)
    }
}