package com.dleibovych.epictale.game

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

import com.dleibovych.epictale.R
import com.dleibovych.epictale.TheTaleApplication
import com.dleibovych.epictale.api.cache.RequestCacheManager
import com.dleibovych.epictale.fragment.GameInfoFragment
import com.dleibovych.epictale.login.LoginActivity
import com.dleibovych.epictale.util.PreferencesManager
import com.dleibovych.epictale.util.TextToSpeechUtils
import com.dleibovych.epictale.util.UiUtils
import com.dleibovych.epictale.util.onscreen.OnscreenPart
import org.thetale.api.models.GameInfo

import javax.inject.Inject


class MainActivity : AppCompatActivity(),
        GameNavigation,
        GameView {

    @Inject
    lateinit var navigationProvider: GameNavigationProvider
    @Inject
    lateinit var presenter: GamePresenter


    /**
     * Used to store the last screen title. For use in [.restoreActionBar].
     */
    var isPaused: Boolean = false
        private set

    var menu: Menu? = null
        private set

    private var accountNameTextView: TextView? = null
    private var timeTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        TheTaleApplication
                .componentProvider
                .gameComponent!!
                .inject(this)

        navigationProvider.navigation = this

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, GameInfoFragment.create())
                    .commit()
        }
    }

    override fun onStart() {
        super.onStart()

        if (PreferencesManager.isReadAloudConfirmed()) {
            TextToSpeechUtils.init(TheTaleApplication.context, null)
        }
    }

    override fun onResume() {
        super.onResume()
        isPaused = false
    }

    override fun onPause() {
        isPaused = true

        TheTaleApplication.onscreenStateWatcher.onscreenStateChange(OnscreenPart.MAIN, false)
        TextToSpeechUtils.pause()
        RequestCacheManager.invalidate()

        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        isPaused = true
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()

        TextToSpeechUtils.destroy()

        navigationProvider.navigation = null
        if (isFinishing) {
            presenter.dispose()
            TheTaleApplication.componentProvider.gameComponent = null
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("InflateParams")
    fun onRefreshStarted() {
    }

    fun onRefreshFinished() {
    }

    fun onDataRefresh() {
        UiUtils.setText(accountNameTextView, PreferencesManager.getAccountName())

        presenter.reload()
    }

    override fun setGameInfo(info: GameInfo) {
        UiUtils.setText(timeTextView, String.format("%s %s", info.turn.verboseDate, info.turn.verboseTime))
    }

    override fun showError() {
        UiUtils.setText(timeTextView, null)
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
