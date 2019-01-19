package org.thetale.auth.steps.thirdparty

import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.thetale.auth.LoginNavigationProvider
import org.thetale.api.TheTaleService
import org.thetale.api.URL
import org.thetale.api.models.isAcceptedAuth
import org.thetale.api.readDataOrThrow
import org.thetale.core.PresenterState

class LoginThirdPartyPresenter(private val service: TheTaleService,
                               private val navigationProvider: LoginNavigationProvider,
                               private val appName: String,
                               private val appDescription: String,
                               private val about: String) {

    lateinit var view: LoginThirdPartyView

    private lateinit var link: String
    private val state = PresenterState { loginThirdParty(appName, appDescription, about) }

    private var thirdPartyLinkJob: Job? = null
    private var thirdPartyStatusJob: Job? = null

    private fun loginThirdParty(appName: String, appInfo: String, appDescription: String) {
        thirdPartyLinkJob = GlobalScope.launch(Main) {
            state.apply { view.showProgress() }

            val thirdPartLink = service.login(appName, appInfo, appDescription).readDataOrThrow()!!
            link = thirdPartLink.authorizationPage
            state.apply {
                view.openThirdPartyLink("$URL$link")
                state.apply { view.hideProgress() }
            }
        }
    }

    fun start() {
        state.start()

        state.clear()
    }

    fun stop() {
        state.stop()
    }

    fun checkAuthorisation() {
        thirdPartyStatusJob = GlobalScope.launch(Main) {
            state.apply { view.showProgress() }
            val state = service.authorizationState().readDataOrThrow()!!
            if (state.isAcceptedAuth()) {
                navigationProvider.navigation?.openApp()
            } else {
                state.apply { view.showError() }
            }

            state.apply { view.hideProgress() }
        }
    }

    fun openLink() {
        view.openThirdPartyLink("$URL$link")
        state.apply { view.hideError() }
    }

    fun dismiss() {
        thirdPartyStatusJob?.cancel()
    }
}
