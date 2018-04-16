package org.thetale.auth.steps.thirdparty

import com.dleibovych.epictale.PresenterState
import org.thetale.auth.LoginNavigationProvider
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.TheTaleService
import org.thetale.api.URL
import org.thetale.api.call
import org.thetale.api.models.isAcceptedAuth

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
        thirdPartyLinkJob = launch(UI) {
            state.apply { view.showProgress() }

            val thirdPartLink = service.login(appName, appInfo, appDescription).call()
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
        thirdPartyStatusJob = launch(UI) {
            state.apply { view.showProgress() }
            val state = service.authorizationState().call()
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
