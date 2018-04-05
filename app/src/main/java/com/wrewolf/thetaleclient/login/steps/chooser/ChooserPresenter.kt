package com.wrewolf.thetaleclient.login.steps.chooser

import com.wrewolf.thetaleclient.PresenterState
import com.wrewolf.thetaleclient.login.LoginNavigation
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.TheTaleService
import org.thetale.api.call

class ChooserPresenter(private val service: TheTaleService,
                       private val navigation: LoginNavigation) {

    internal lateinit var view: ChooserView

    private var thirdPartyDeferred: Job? = null

    private val state = PresenterState { view.hideProgress() }

    fun thirdPartyLogin(appName: String, appInfo: String, appDescription: String) {
        thirdPartyDeferred = launch(UI) {
            state.apply { view.showProgress() }
            val link = service.login(appName, appInfo, appDescription).call()
            state.apply { view.hideProgress() }

            navigation.showThirdParty(link.authorizationPage)
        }
    }

    fun credentialsLogin() {
        navigation.showCredentials()
    }

    fun start() {
        state.start()
    }

    fun stop() {
        state.stop()
    }

    fun dispose() {
        thirdPartyDeferred?.cancel()

        state.clear()
    }
}