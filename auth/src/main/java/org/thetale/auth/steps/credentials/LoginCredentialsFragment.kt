package org.thetale.auth.steps.credentials

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_login_credentials.*
import org.thetale.api.URL_PASSWORD_REMIND
import org.thetale.api.URL_REGISTRATION
import org.thetale.auth.R
import org.thetale.auth.di.LoginComponent
import org.thetale.auth.di.LoginComponentProvider
import org.thetale.core.openUrl
import javax.inject.Inject

class LoginCredentialsFragment : Fragment(), LoginCredentialsView {

    @Inject
    lateinit var presenter: LoginCredentialsPresenter

    lateinit var componentProvider: LoginComponentProvider

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        componentProvider = (activity?.application as LoginComponentProvider)
        componentProvider.provideLoginComponent()?.inject(this)

        presenter.view = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_credentials, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginPasswordView.onLoginClick {
            presenter.loginWithCredentials(it.email, it.password)
        }

        loginPasswordView.onRemindPasswordClick(View.OnClickListener {
            openUrl(activity!!, URL_PASSWORD_REMIND)
        })

        registration.setOnClickListener {
            openUrl(activity!!, URL_REGISTRATION)
        }

        loginThirdParty.setOnClickListener {
            presenter.navigateToThirdParty()
        }
    }

    override fun onStart() {
        super.onStart()

        presenter.start()
    }

    override fun onStop() {
        super.onStop()

        presenter.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.view = null
    }

    override fun onDestroy() {
        super.onDestroy()

        if (activity!!.isFinishing) {
            presenter.dispose()
            componentProvider.cleanLoginComponent()
        }
    }

    override fun showProgress() {
        progress.visibility = VISIBLE
        loginPasswordView.visibility = GONE
    }

    override fun hideProgress() {
        progress.visibility = GONE
        loginPasswordView.visibility = VISIBLE
    }

    override fun showError() {
        progress.visibility = GONE
        loginPasswordView.visibility = VISIBLE

        loginPasswordView.setPasswordError(getString(R.string.common_error))
    }

    override fun showLoginError(loginError: String?, passwordError: String?) {
        progress.visibility = GONE
        loginPasswordView.visibility = VISIBLE

        loginPasswordView.setEmailError(loginError)
        loginPasswordView.setPasswordError(passwordError)
    }

    companion object {

        fun create() = LoginCredentialsFragment()
    }
}