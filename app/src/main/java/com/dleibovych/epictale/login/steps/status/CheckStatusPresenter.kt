package com.dleibovych.epictale.login.steps.status

import com.dleibovych.epictale.PresenterState
import com.dleibovych.epictale.login.LoginNavigationProvider
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.TheTaleService
import org.thetale.api.call

class CheckStatusPresenter(private val service: TheTaleService,
                           private val navigationProvider: LoginNavigationProvider) {

    var view: CheckStatusView? = null

    private val state = PresenterState { checkAppInfo() }
    private var appInfoJob: Job? = null

    fun start() {
        state.start()
    }

    fun stop() {
        state.stop()
    }

    fun retry() {
        state.apply { checkAppInfo() }
    }

    fun dispose() {
        appInfoJob?.cancel()
    }

    private fun checkAppInfo() {
        appInfoJob = launch(UI) {
            try {
                state.apply { view?.showLoading() }

                val info = service.info().call()
                if (info.accountId == null) {
                    navigationProvider.navigation?.showCredentials()
                } else {
                    navigationProvider.navigation?.openApp()
                }

                state.apply { view?.hideLoading() }
            } catch (e: Exception) {
                state.apply { view?.showError() }
            }
        }
    }
}