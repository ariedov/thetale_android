package com.dleibovych.epictale.game.profile

import com.dleibovych.epictale.game.GameNavigation
import com.dleibovych.epictale.game.GameNavigationProvider
import com.dleibovych.epictale.game.data.GameInfoProvider
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.thetale.api.TheTaleService
import org.thetale.api.readDataOrThrow
import org.thetale.core.PresenterState
import java.net.CookieStore

class ProfilePresenter(private val service: TheTaleService,
                       private val cookieStore: CookieStore,
                       private val gameInfoProvider: GameInfoProvider,
                       private val navigationProvider: GameNavigationProvider) {

    private val state = PresenterState { loadProfileInfo() }
    private var profileJob: Job? = null
    private var logoutJob: Job? = null

    var view: ProfileView? = null

    fun start() {
        state.start()
    }

    fun stop() {
        state.stop()
    }

    private fun loadProfileInfo() {
        profileJob = GlobalScope.launch(Main) {
            try {
                state.apply { view?.showProgress() }
                val gameInfo = gameInfoProvider.getInfo().await()
                val accountInfo = service.getAccount(gameInfo.account!!.id).readDataOrThrow()!!
                state.apply { view?.showAccountInfo(accountInfo) }
            } catch (e: Exception) {
                state.apply { view?.showError() }
            }
        }
    }

    fun logout() {
        logoutJob = GlobalScope.launch(Main) {
            try {
                view?.showProgress()
                service.logout().join()
                cookieStore.removeAll()
                view?.hideProgress()
                navigationProvider.navigation?.showLogin()
            } catch (e: Exception) {
                loadProfileInfo()
            }
        }
    }

    fun retry() {
        state.apply { loadProfileInfo() }
    }

    fun dispose() {
        profileJob?.cancel()
        logoutJob?.cancel()
    }
}