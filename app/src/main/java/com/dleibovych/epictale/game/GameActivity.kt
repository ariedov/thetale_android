package com.dleibovych.epictale.game

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView

import com.dleibovych.epictale.DataViewMode
import com.dleibovych.epictale.DrawerItem
import com.dleibovych.epictale.R
import com.dleibovych.epictale.TheTaleClientApplication
import com.dleibovych.epictale.api.ApiResponseCallback
import com.dleibovych.epictale.api.cache.RequestCacheManager
import com.dleibovych.epictale.api.cache.prerequisite.InfoPrerequisiteRequest
import com.dleibovych.epictale.api.cache.prerequisite.PrerequisiteRequest
import com.dleibovych.epictale.api.request.GameInfoRequest
import com.dleibovych.epictale.api.request.LogoutRequest
import com.dleibovych.epictale.api.response.CommonResponse
import com.dleibovych.epictale.api.response.GameInfoResponse
import com.dleibovych.epictale.api.response.InfoResponse
import com.dleibovych.epictale.fragment.GameFragment
import com.dleibovych.epictale.fragment.NavigationDrawerFragment
import com.dleibovych.epictale.fragment.Refreshable
import com.dleibovych.epictale.fragment.WrapperFragment
import com.dleibovych.epictale.login.LoginActivity
import com.dleibovych.epictale.util.DialogUtils
import com.dleibovych.epictale.util.HistoryStack
import com.dleibovych.epictale.util.PreferencesManager
import com.dleibovych.epictale.util.TextToSpeechUtils
import com.dleibovych.epictale.util.UiUtils
import com.dleibovych.epictale.util.WebsiteUtils
import com.dleibovych.epictale.util.onscreen.OnscreenPart

import java.net.CookieManager

import javax.inject.Inject

import okhttp3.OkHttpClient

class GameActivity : AppCompatActivity(), NavigationDrawerFragment.NavigationDrawerCallbacks, GameNavigation {

    @Inject lateinit var navigationProvider: GameNavigationProvider
    @Inject lateinit var client: OkHttpClient
    @Inject lateinit var manager: CookieManager

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private var mNavigationDrawerFragment: NavigationDrawerFragment? = null

    /**
     * Used to store the last screen title. For use in [.restoreActionBar].
     */
    private var mTitle: CharSequence? = null
    private var currentItem: DrawerItem? = null
    private var history: HistoryStack<DrawerItem>? = null
    var isPaused: Boolean = false
        private set

    var menu: Menu? = null
        private set

    private var accountNameTextView: TextView? = null
    private var timeTextView: TextView? = null
    private var drawerItemInfoView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        TheTaleClientApplication
                .getComponentProvider()
                .gameComponent!!
                .inject(this)

        navigationProvider.navigation = this

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mNavigationDrawerFragment = supportFragmentManager.findFragmentById(R.id.navigation_drawer) as NavigationDrawerFragment
        mTitle = title

        // set up the drawer
        mNavigationDrawerFragment!!.setUp(
                R.id.navigation_drawer,
                findViewById(R.id.drawer_layout))

        accountNameTextView = findViewById(R.id.drawer_account_name)
        timeTextView = findViewById(R.id.drawer_time)
        drawerItemInfoView = findViewById(DrawerItem.PROFILE.viewResId)

        history = HistoryStack(DrawerItem.values().size)
        var tabIndex = DrawerItem.GAME.ordinal
        if (savedInstanceState != null) {
            tabIndex = savedInstanceState.getInt(KEY_DRAWER_TAB_INDEX, tabIndex)
        }
        onNavigationDrawerItemSelected(DrawerItem.values()[tabIndex])
    }

    override fun onStart() {
        super.onStart()

        if (PreferencesManager.isReadAloudConfirmed()) {
            TextToSpeechUtils.init(TheTaleClientApplication.getContext(), null)
        }
    }

    override fun onResume() {
        super.onResume()
        isPaused = false

        if (PreferencesManager.shouldExit()) {
            PreferencesManager.setShouldExit(false)
            finish()
        }

        val intent = intent
        var tabIndex = -1
        if (intent != null) {
            if (intent.hasExtra(KEY_GAME_TAB_INDEX)) {
                onNavigationDrawerItemSelected(DrawerItem.GAME)
                tabIndex = intent.getIntExtra(KEY_GAME_TAB_INDEX, GameFragment.GamePage.GAME_INFO.ordinal)
                intent.removeExtra(KEY_GAME_TAB_INDEX)
            }

            if (intent.getBooleanExtra(KEY_SHOULD_RESET_WATCHING_ACCOUNT, false)) {
                PreferencesManager.setWatchingAccount(0, null)
                intent.removeExtra(KEY_SHOULD_RESET_WATCHING_ACCOUNT)
            }
        }

        val fragment = supportFragmentManager.findFragmentByTag(currentItem!!.fragmentTag)
        if (tabIndex != -1) {
            val gamePage = GameFragment.GamePage.values()[tabIndex]
            if (fragment is GameFragment) {
                fragment.setCurrentPage(gamePage)
            } else {
                PreferencesManager.setDesiredGamePage(gamePage)
            }
        }
        UiUtils.callOnscreenStateChange(fragment, true)

        TheTaleClientApplication.getOnscreenStateWatcher().onscreenStateChange(OnscreenPart.MAIN, true)
    }

    override fun onPause() {
        isPaused = true

        TheTaleClientApplication.getOnscreenStateWatcher().onscreenStateChange(OnscreenPart.MAIN, false)
        TextToSpeechUtils.pause()
        RequestCacheManager.invalidate()
        UiUtils.callOnscreenStateChange(supportFragmentManager.findFragmentByTag(currentItem!!.fragmentTag), false)

        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        isPaused = true
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_DRAWER_TAB_INDEX, currentItem!!.ordinal)
    }

    override fun onDestroy() {
        super.onDestroy()

        TextToSpeechUtils.destroy()

        navigationProvider.navigation = null
        if (isFinishing) {
            TheTaleClientApplication.getComponentProvider().gameComponent = null
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onNavigationDrawerItemSelected(item: DrawerItem) {
        if (item != currentItem) {
            when (item) {
                DrawerItem.PROFILE -> DialogUtils.showChoiceDialog(supportFragmentManager, getString(R.string.drawer_title_site),
                        arrayOf(getString(R.string.drawer_dialog_profile_item_keeper), getString(R.string.drawer_dialog_profile_item_hero))
                ) { position ->
                    InfoPrerequisiteRequest(client, manager, {
                        val accountId = PreferencesManager.getAccountId()
                        if (accountId == 0) {
                            if (!isPaused) {
                                DialogUtils.showCommonErrorDialog(supportFragmentManager, this@GameActivity)
                            }
                        } else {
                            when (position) {
                                0 -> startActivity(UiUtils.getOpenLinkIntent(String.format(WebsiteUtils.URL_PROFILE_KEEPER, accountId)))

                                1 -> startActivity(UiUtils.getOpenLinkIntent(String.format(WebsiteUtils.URL_PROFILE_HERO, accountId)))

                                else -> if (!isPaused) {
                                    DialogUtils.showCommonErrorDialog(supportFragmentManager, this@GameActivity)
                                }
                            }
                        }
                    }, object : PrerequisiteRequest.ErrorCallback<InfoResponse>() {
                        override fun processError(response: InfoResponse) {
                            if (!isPaused) {
                                DialogUtils.showCommonErrorDialog(supportFragmentManager, this@GameActivity)
                            }
                        }
                    }, null).execute()
                }

                DrawerItem.SITE -> startActivity(UiUtils.getOpenLinkIntent(WebsiteUtils.URL_GAME))

                DrawerItem.LOGOUT -> {
                    PreferencesManager.setSession("")

                    val fragment = supportFragmentManager.findFragmentByTag(currentItem!!.fragmentTag)
                    if (fragment is WrapperFragment) {
                        fragment.setMode(DataViewMode.LOADING)
                    }

                    LogoutRequest(client, manager).execute(object : ApiResponseCallback<CommonResponse> {
                        override fun processResponse(response: CommonResponse) {
                            startActivity(Intent(this@GameActivity, LoginActivity::class.java))
                            finish()
                        }

                        override fun processError(response: CommonResponse) {
                            if (fragment is WrapperFragment) {
                                fragment.setError(response.errorMessage)
                            }
                        }
                    })
                }

                DrawerItem.ABOUT -> DialogUtils.showAboutDialog(supportFragmentManager)

                else -> {
                    val fragmentManager = supportFragmentManager
                    var oldFragment: Fragment? = fragmentManager.findFragmentByTag(item.fragmentTag)
                    if (oldFragment == null) {
                        oldFragment = item.fragment
                        fragmentManager.beginTransaction()
                                .replace(R.id.container, oldFragment, item.fragmentTag)
                                .commit()
                    } else if (oldFragment.isDetached) {
                        fragmentManager.beginTransaction()
                                .attach(oldFragment)
                                .commit()
                    }

                    if (currentItem != null) {
                        UiUtils.callOnscreenStateChange(supportFragmentManager.findFragmentByTag(currentItem!!.fragmentTag), false)
                    }
                    UiUtils.callOnscreenStateChange(oldFragment, true)

                    currentItem = item
                    mTitle = getString(currentItem!!.titleResId)
                    history!!.set(currentItem)
                    supportInvalidateOptionsMenu()
                }
            }
        }
    }

    fun restoreActionBar() {
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(true)
        actionBar.title = mTitle
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (!mNavigationDrawerFragment!!.isDrawerOpen && currentItem!!.menuResId != 0) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            this.menu = menu
            menuInflater.inflate(currentItem!!.menuResId, menu)
            restoreActionBar()
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun refresh() {
        onRefreshStarted()
        val fragment = supportFragmentManager.findFragmentByTag(currentItem!!.fragmentTag)
        if (fragment is Refreshable) {
            (fragment as Refreshable).refresh(true)
        }
    }

    fun refreshGameAdjacentFragments() {
        if (currentItem == DrawerItem.GAME) {
            val fragment = supportFragmentManager.findFragmentByTag(currentItem!!.fragmentTag)
            if (fragment is GameFragment) {
                fragment.refreshAdjacentFragments()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                refresh()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun onRefreshStarted() {
        if (menu != null) {
            val itemRefresh = menu!!.findItem(R.id.action_refresh)
            if (itemRefresh != null) {
                MenuItemCompat.setActionView(itemRefresh, layoutInflater.inflate(R.layout.menu_progress, null))
            }
        }
    }

    fun onRefreshFinished() {
        if (menu != null) {
            val itemRefresh = menu!!.findItem(R.id.action_refresh)
            if (itemRefresh != null) {
                MenuItemCompat.setActionView(itemRefresh, null)
            }
        }
    }

    fun onDataRefresh() {
        InfoPrerequisiteRequest(client, manager, {
            drawerItemInfoView!!.visibility = View.VISIBLE
            UiUtils.setText(accountNameTextView, PreferencesManager.getAccountName())
            GameInfoRequest(client, manager, false).execute(object : ApiResponseCallback<GameInfoResponse> {
                override fun processResponse(response: GameInfoResponse) {
                    UiUtils.setText(timeTextView, String.format("%s %s", response.turnInfo.verboseDate, response.turnInfo.verboseTime))
                }

                override fun processError(response: GameInfoResponse) {
                    UiUtils.setText(timeTextView, null)
                }
            }, true)
        }, object : PrerequisiteRequest.ErrorCallback<InfoResponse>() {
            override fun processError(response: InfoResponse) {
                drawerItemInfoView!!.visibility = View.GONE
                UiUtils.setText(accountNameTextView, null)
                UiUtils.setText(timeTextView, null)
            }
        }, null).execute()
    }

    override fun onBackPressed() {
        if (mNavigationDrawerFragment!!.isDrawerOpen) {
            mNavigationDrawerFragment!!.closeDrawer()
        } else {
            val drawerItem = history!!.pop()
            if (drawerItem == null) {
                PreferencesManager.setShouldExit(true)
                finish()
            } else {
                onNavigationDrawerItemSelected(drawerItem)
            }
        }
    }

    companion object {

        const val KEY_GAME_TAB_INDEX = "KEY_GAME_TAB_INDEX"
        const val KEY_SHOULD_RESET_WATCHING_ACCOUNT = "KEY_SHOULD_RESET_WATCHING_ACCOUNT"

        private const val KEY_DRAWER_TAB_INDEX = "KEY_DRAWER_TAB_INDEX"
    }

}
