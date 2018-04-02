package com.wrewolf.thetaleclient.login.steps.chooser

import com.wrewolf.thetaleclient.PresenterState
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.TheTaleService
import org.thetale.api.call

class ChooserPresenter(private val service: TheTaleService) {

    private lateinit var view: ChooserView
    private lateinit var navigator: ChooserNavigator

    private var thirdPartyDeferred: Job? = null

    private val state = PresenterState()

    fun thirdPartyLogin(appName: String, appInfo: String, appDescription: String) {
        thirdPartyDeferred = launch(UI) {
            state.apply { view.showProgress() }
            val link = service.login(appName, appInfo, appDescription).call()
            state.apply { view.hideProgress() }

            navigator.openThirdPartyLogin(link.authorizationPage)
        }
    }

    fun credentialsLogin() {
        navigator.openCredentialsLogin()
    }

    fun start() {
        state.apply()
    }

    fun dispose() {
        thirdPartyDeferred?.cancel()

        state.clear()
    }
}