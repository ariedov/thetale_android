package com.dleibovych.epictale.game

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

import com.dleibovych.epictale.R
import com.dleibovych.epictale.TheTaleApplication
import com.dleibovych.epictale.api.cache.RequestCacheManager
import com.dleibovych.epictale.fragment.GameFragment
import com.dleibovych.epictale.fragment.MapFragment
import com.dleibovych.epictale.fragment.ProfileFragment
import com.dleibovych.epictale.game.di.GameComponentProvider
import com.dleibovych.epictale.util.PreferencesManager
import com.dleibovych.epictale.util.TextToSpeechUtils
import com.dleibovych.epictale.util.onscreen.OnscreenPart
import kotlinx.android.synthetic.main.activity_main.*
import org.thetale.api.models.GameInfo
import org.thetale.auth.LoginActivity

import javax.inject.Inject


class MainActivity : AppCompatActivity(),
        GameNavigation,
        GameView {

    @Inject
    lateinit var navigationProvider: GameNavigationProvider
    @Inject
    lateinit var presenter: GamePresenter

    lateinit var componentProvider: GameComponentProvider

    override fun onCreate(savedInstanceState: Bundle?) {

        componentProvider = (application as GameComponentProvider)
        componentProvider.provideGameComponent()?.inject(this)

        navigationProvider.navigation = this

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, GameFragment.create())
                    .commit()
        }

        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.game -> {
                    showFragment(GameFragment.create())
                    true
                }
                R.id.map -> {
                    showFragment(MapFragment())
                    true
                }
                R.id.profile -> {
                    showFragment(ProfileFragment())
                    true
                }
                else -> false
            }
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
            presenter.dispose()
            componentProvider.cleanGameComponent()
        }
    }

    override fun setGameInfo(info: GameInfo) {
    }

    override fun showError() {
    }

    override fun showLogin() {
        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }

    companion object {

        const val KEY_GAME_TAB_INDEX = "KEY_GAME_TAB_INDEX"
        const val KEY_SHOULD_RESET_WATCHING_ACCOUNT = "KEY_SHOULD_RESET_WATCHING_ACCOUNT"

        private const val KEY_DRAWER_TAB_INDEX = "KEY_DRAWER_TAB_INDEX"
    }

}
