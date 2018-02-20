package com.wrewolf.thetaleclient.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.GONE
import android.view.View.VISIBLE

import com.wrewolf.thetaleclient.R
import com.wrewolf.thetaleclient.TheTaleClientApplication
import com.wrewolf.thetaleclient.activity.MainActivity
import com.wrewolf.thetaleclient.util.UiUtils
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.*

import java.net.CookieManager

import javax.inject.Inject

import okhttp3.OkHttpClient

class LoginActivity : AppCompatActivity(), LoginNavigation {

    @Inject
    lateinit var presenter: LoginPresenter
    @Inject
    lateinit var client: OkHttpClient
    @Inject
    lateinit var cookieManager: CookieManager

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TheTaleClientApplication.getComponentProvider()
                .loginComponent!!
                .inject(this)

        presenter.navigator = this

        setContentView(R.layout.activity_login)

    }

    override fun onStart() {
        super.onStart()

        disposables.add(
                presenter.viewStates.subscribe {
                    when (it) {
                        LoginState.Initial -> {
                            presenter.checkAppInfo()
                        }
                        LoginState.Loading -> {
                            progressBar.visibility = VISIBLE
                            content.visibility = GONE
                            error.visibility = GONE
                        }
                        LoginState.Chooser -> {
                            content.visibility = VISIBLE
                            loginContentStart.visibility = VISIBLE
                            progressBar.visibility = GONE
                            loginPassword.visibility = GONE
                            error.visibility = GONE
                        }
                        LoginState.Credentials -> {
                            content.visibility = VISIBLE
                            loginPassword.visibility = VISIBLE
                            loginContentStart.visibility = GONE
                            progressBar.visibility = GONE
                            error.visibility = GONE
                        }
                        is LoginState.CredentialsError -> {
                            applyCredentialError(it.loginError, it.passwordError)

                            content.visibility = VISIBLE
                            loginPassword.visibility = VISIBLE
                            loginContentStart.visibility = GONE
                            progressBar.visibility = GONE
                            error.visibility = GONE
                        }
                        is LoginState.Error -> {
                            error.visibility = VISIBLE
                            content.visibility = GONE
                            loginPassword.visibility = GONE
                            loginContentStart.visibility = GONE
                            progressBar.visibility = GONE
                        }
                    }
                })

        disposables.add(
                loginContentStart
                        .loginWithCredentialsEvents()
                        .subscribe {
                            presenter.viewStates.accept(LoginState.Credentials)
                        }
        )
    }

    private fun applyCredentialError(loginError: String?, passwordError: String?) {
        if (loginError != null) {
            loginPassword.setEmailError(loginError)
        }
        if (passwordError != null) {
            loginPassword.setPasswordError(passwordError)
        }
    }

    override fun onStop() {
        super.onStop()
        disposables.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) {
            TheTaleClientApplication.getComponentProvider().loginComponent = null
            presenter.dispose()
        }
    }

    override fun startRegistration() {
        startActivity(UiUtils.getOpenLinkIntent(URL_REGISTRATION))
    }

    override fun proceedToGame() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtras(getIntent())

        startActivity(intent)
        finish()
    }

    companion object {

        private val URL_HOME = "http://the-tale.org/?action=the-tale-client"
        private val URL_REGISTRATION = "http://the-tale.org/accounts/registration/fast?action=the-tale-client"
        private val URL_PASSWORD_REMIND = "http://the-tale.org/accounts/profile/reset-password?action=the-tale-client"
        private val THIRD_PARTY_AUTH_STATE_TIMEOUT: Long = 10000
    }
}
