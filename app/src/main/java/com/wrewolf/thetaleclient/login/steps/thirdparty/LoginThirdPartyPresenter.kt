package com.wrewolf.thetaleclient.login.steps.thirdparty

import com.wrewolf.thetaleclient.PresenterState
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.thetale.api.TheTaleService
import org.thetale.api.URL
import org.thetale.api.call
import org.thetale.api.models.isAcceptedAuth

class LoginThirdPartyPresenter(private val service: TheTaleService) {

    lateinit var link: String
    lateinit var view: LoginThirdPartyView

    private val state = PresenterState { openLink() }

    private var thirdPartyStatusJob: Job? = null

    fun start() {
        state.apply()
        state.clear()
    }

    fun checkAuthorisation() {
        thirdPartyStatusJob = launch(UI) {
            state.apply { view.showProgress() }
            val state = service.authorizationState().call()
            if (state.isAcceptedAuth()) {

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
