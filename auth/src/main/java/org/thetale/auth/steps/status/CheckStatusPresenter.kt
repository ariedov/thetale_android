package org.thetale.auth.steps.status

import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.thetale.auth.LoginNavigationProvider
import org.thetale.api.TheTaleService
import org.thetale.api.readDataOrThrow
import org.thetale.core.PresenterState

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
        appInfoJob = GlobalScope.launch(Main) {
            try {
                state.apply { view?.showLoading() }
                val info = service.info().readDataOrThrow()!!
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