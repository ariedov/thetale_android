package com.dleibovych.epictale.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import com.dleibovych.epictale.R
import com.dleibovych.epictale.TheTaleApplication
import com.dleibovych.epictale.game.MainActivity
import com.dleibovych.epictale.util.PreferencesManager
import com.dleibovych.epictale.util.TextToSpeechUtils
import com.dleibovych.epictale.util.UiUtils
import com.dleibovych.epictale.util.onscreen.OnscreenStateListener

import java.net.CookieManager
import java.util.HashMap

import javax.inject.Inject

import okhttp3.OkHttpClient

/**
 * @author Hamster
 * @since 05.10.2014
 */
class GameFragment : Fragment(), Refreshable, OnscreenStateListener {

    @Inject
    lateinit var client: OkHttpClient
    @Inject
    lateinit var manager: CookieManager

    private var viewPager: ViewPager? = null
    private var findPlayerContainer: View? = null

    private var currentPageIndex: Int = 0
    private var shouldCallOnscreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity!!.application as TheTaleApplication)
                .getApplicationComponent()
                .inject(this)

        val rootView = inflater.inflate(R.layout.fragment_game, container, false)

        viewPager = rootView.findViewById(R.id.fragment_game_pager)
        viewPager!!.adapter = GamePagerAdapter(activity!!.supportFragmentManager)

        val gamePage = PreferencesManager.getDesiredGamePage()
        if (gamePage != null) {
            setCurrentPage(gamePage)
            PreferencesManager.setDesiredGamePage(null)
        } else if (savedInstanceState != null) {
            viewPager!!.currentItem = savedInstanceState.getInt(KEY_PAGE_INDEX, 0)
        }

        findPlayerContainer = rootView.findViewById(R.id.fragment_game_find_player)
        UiUtils.setupFindPlayerContainer(client, manager, findPlayerContainer!!, this, this, activity as MainActivity?)

        return rootView
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(KEY_PAGE_INDEX, viewPager!!.currentItem)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        updateMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_read_aloud -> if (PreferencesManager.isReadAloudConfirmed()) {
                val wasReadAloudEnabled: Boolean
                when (GamePage.values()[currentPageIndex]) {
                    GameFragment.GamePage.GAME_INFO -> {
                        wasReadAloudEnabled = PreferencesManager.isJournalReadAloudEnabled()
                        PreferencesManager.setJournalReadAloudEnabled(!wasReadAloudEnabled)
                    }

                    GameFragment.GamePage.DIARY -> {
                        wasReadAloudEnabled = PreferencesManager.isDiaryReadAloudEnabled()
                        PreferencesManager.setDiaryReadAloudEnabled(!wasReadAloudEnabled)
                    }

                    else -> return super.onOptionsItemSelected(item)
                }

                if (wasReadAloudEnabled) {
                    TextToSpeechUtils.pause()
                }
                updateMenu()
                return true
            } else {
                return super.onOptionsItemSelected(item)
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun updateMenu() {
        val readAloudMenuItem = UiUtils.getMenuItem(activity, R.id.action_read_aloud)
        if (readAloudMenuItem != null) {
            var isVisible = false
            var isOn = false
            when (GamePage.values()[currentPageIndex]) {
                GameFragment.GamePage.GAME_INFO -> {
                    isVisible = PreferencesManager.isReadAloudConfirmed()
                    if (isVisible) {
                        isOn = PreferencesManager.isJournalReadAloudEnabled()
                    }
                }

                GameFragment.GamePage.DIARY -> {
                    isVisible = PreferencesManager.isReadAloudConfirmed()
                    if (isVisible) {
                        isOn = PreferencesManager.isDiaryReadAloudEnabled()
                    }
                }
            }

            readAloudMenuItem.isVisible = isVisible
            if (isVisible) {
                readAloudMenuItem.setIcon(if (isOn) R.drawable.ic_volume_small else R.drawable.ic_volume_off_small)
            }
        }
    }

    override fun refresh(isGlobal: Boolean) {
        val fragment = getPageFragment(viewPager!!.currentItem)
        if (fragment is WrapperFragment) {
            fragment.refresh(isGlobal)
        }
        UiUtils.setupFindPlayerContainer(client, manager, findPlayerContainer!!, this, this, activity as MainActivity?)
    }

    private fun refreshPageFragment(position: Int) {
        val fragment = getPageFragment(position)
        if (fragment is WrapperFragment) {
            fragment.refresh(true)
        }
    }

    fun refreshAdjacentFragments() {
        if (currentPageIndex > 0) {
            refreshPageFragment(currentPageIndex - 1)
        }
        if (currentPageIndex < GamePage.values().size - 1) {
            refreshPageFragment(currentPageIndex + 1)
        }
    }

    fun setCurrentPage(page: GamePage?) {
        if (page != null) {
            viewPager!!.currentItem = page.ordinal
        }
    }

    private fun getPageFragment(position: Int): Fragment? {
        if (viewPager == null) {
            return null
        }

        val pagerAdapter = viewPager!!.adapter
        return if (pagerAdapter is GamePagerAdapter) {
            pagerAdapter.getFragment(position)
        } else {
            null
        }
    }

    override fun onOffscreen() {
        UiUtils.callOnscreenStateChange(getPageFragment(currentPageIndex), false)
    }

    override fun onOnscreen() {
        val fragment = getPageFragment(currentPageIndex)
        if (fragment == null) {
            shouldCallOnscreen = true
        } else {
            UiUtils.callOnscreenStateChange(fragment, true)
            shouldCallOnscreen = false
        }

        if (findPlayerContainer != null) {
            UiUtils.setupFindPlayerContainer(client, manager, findPlayerContainer!!, this, this, activity as MainActivity?)
        }
    }

    enum class GamePage private constructor(val titleResId: Int) {

        GAME_INFO(R.string.game_title_info) {
            override val fragment: Fragment
                get() = GameInfoFragment()
        },
        QUESTS(R.string.game_title_quests) {
            override val fragment: Fragment
                get() = QuestsFragment()
        },
        EQUIPMENT(R.string.game_title_equipment) {
            override val fragment: Fragment
                get() = EquipmentFragment()
        },
        CARDS(R.string.game_title_cards) {
            override val fragment: Fragment
                get() = CardsFragment()
        },
        DIARY(R.string.game_title_diary) {
            override val fragment: Fragment
                get() = DiaryFragment()
        },
        PROFILE(R.string.game_title_profile) {
            override val fragment: Fragment
                get() = ProfileFragment()
        };

        abstract val fragment: Fragment

    }

    private inner class GamePagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

        private val fragments = HashMap<Int, Fragment>()

        override fun getPageTitle(position: Int): CharSequence? {
            return getString(GamePage.values()[position].titleResId)
        }

        override fun getItem(i: Int): Fragment {
            return GamePage.values()[i].fragment
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val fragment = super.instantiateItem(container, position) as Fragment
            fragments[position] = fragment
            if (shouldCallOnscreen) {
                UiUtils.callOnscreenStateChange(fragment, true)
                shouldCallOnscreen = false
            }
            return fragment
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            fragments.remove(position)
            super.destroyItem(container, position, `object`)
        }

        override fun getCount(): Int {
            return GamePage.values().size
        }

        fun getFragment(position: Int): Fragment {
            return fragments[position]!!
        }

    }

    companion object {

        private val KEY_PAGE_INDEX = "KEY_PAGE_INDEX"

        fun create() = GameInfoFragment()
    }

}
