package com.dleibovych.epictale.game

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

import com.dleibovych.epictale.R
import com.dleibovych.epictale.TheTaleApplication
import com.dleibovych.epictale.api.cache.RequestCacheManager
import com.dleibovych.epictale.game.map.MapFragment
import com.dleibovych.epictale.game.profile.ProfileFragment
import com.dleibovych.epictale.game.di.GameComponentProvider
import com.dleibovych.epictale.game.gameinfo.GameInfoFragment
import com.dleibovych.epictale.game.quests.QuestsFragment
import com.dleibovych.epictale.util.PreferencesManager
import com.dleibovych.epictale.util.TextToSpeechUtils
import com.dleibovych.epictale.util.onscreen.OnscreenPart
import kotlinx.android.synthetic.main.activity_main.*
import org.thetale.auth.LoginActivity

import javax.inject.Inject

class MainActivity : AppCompatActivity(), GameNavigation {

    @Inject
    lateinit var navigationProvider: GameNavigationProvider

    private lateinit var componentProvider: GameComponentProvider

    private val gameInfo by lazy { GameInfoFragment.create() }
    private val map by lazy { MapFragment() }
    private val quests by lazy { QuestsFragment.create() }
    private val profile by lazy { ProfileFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {

        componentProvider = (application as GameComponentProvider)
        componentProvider.provideGameComponent()?.inject(this)

        navigationProvider.navigation = this

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, GameInfoFragment.create())
                    .commit()
        }

        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.game -> { showFragment(gameInfo) }
                R.id.map -> { showFragment(map) }
                R.id.quests -> { showFragment(quests) }
                R.id.profile -> { showFragment(profile) }
            }
            true
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
    }

    override fun onStart() {
        super.onStart()

        if (PreferencesManager.isReadAloudConfirmed()) {
            TextToSpeechUtils.init(TheTaleApplication.context, null)
        }
    }

    override fun onPause() {
        TheTaleApplication.onscreenStateWatcher?.onscreenStateChange(OnscreenPart.MAIN, false)
        TextToSpeechUtils.pause()
        RequestCacheManager.invalidate()

        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

        TextToSpeechUtils.destroy()

        navigationProvider.navigation = null
        if (isFinishing) {
            componentProvider.cleanGameComponent()
        }
    }

    override fun showLogin() {
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }

    companion object {

        const val KEY_GAME_TAB_INDEX = "KEY_GAME_TAB_INDEX"
        const val KEY_SHOULD_RESET_WATCHING_ACCOUNT = "KEY_SHOULD_RESET_WATCHING_ACCOUNT"
    }

}
