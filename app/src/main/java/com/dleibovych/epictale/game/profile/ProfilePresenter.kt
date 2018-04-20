package com.dleibovych.epictale.game.profile

import com.dleibovych.epictale.game.data.GameInfoProvider
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.TheTaleService
import org.thetale.api.readDataOrThrow
import org.thetale.core.PresenterState

class ProfilePresenter(private val service: TheTaleService,
                       private val gameInfoProvider: GameInfoProvider) {

    private val state = PresenterState { loadProfileInfo() }
    private var profileJob: Job? = null

    var view: ProfileView? = null

    fun start() {
        state.start()
    }

    fun stop() {
        state.stop()
    }

    private fun loadProfileInfo() {
        profileJob = launch(UI) {
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

    fun retry() {
        state.apply { loadProfileInfo() }
    }

    fun dispose() {
        profileJob?.cancel()
    }
}