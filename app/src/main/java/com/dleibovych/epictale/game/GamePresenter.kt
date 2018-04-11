package com.dleibovych.epictale.game

import com.dleibovych.epictale.game.data.GameInfoProvider
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.TheTaleService
import java.net.CookieStore

class GamePresenter(private val navigationProvider: GameNavigationProvider,
                    private val provider: GameInfoProvider,
                    private val service: TheTaleService,
                    private val cookieStore: CookieStore) {

    var view: GameView? = null

    private var gameInfoJob: Job? = null
    private var logoutJob: Job? = null

    fun reload() {
        gameInfoJob = launch(UI) {
            try {
                val info = provider.getInfo().await()
                view?.setGameInfo(info)
            } catch (e: Exception) {
                view?.showError()
            }
        }
    }

    fun logout() {
        logoutJob = launch(UI) {
            try {
                service.logout()
                cookieStore.removeAll()
                navigationProvider.navigation?.showLogin()
            } catch (ignored: Exception) {}
        }
    }

    fun dispose() {
        gameInfoJob?.cancel()
    }
}