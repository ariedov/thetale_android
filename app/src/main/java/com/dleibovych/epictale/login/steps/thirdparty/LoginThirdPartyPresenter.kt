package com.dleibovych.epictale.login.steps.thirdparty

import com.dleibovych.epictale.PresenterState
import com.dleibovych.epictale.login.LoginNavigation
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.TheTaleService
import org.thetale.api.URL
import org.thetale.api.call
import org.thetale.api.models.isAcceptedAuth

class LoginThirdPartyPresenter(private val service: TheTaleService,
                               private val navigation: LoginNavigation) {

    lateinit var view: LoginThirdPartyView

    private lateinit var link: String
    private val state = PresenterState()

    private var thirdPartyLinkJob: Job? = null
    private var thirdPartyStatusJob: Job? = null

    fun initAppInfo(appName: String, appDescription: String, about: String) {
        state.apply { loginThirdParty(appName, appDescription, about) }
    }

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
                navigation.openApp()
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
