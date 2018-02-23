package com.wrewolf.thetaleclient.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.View.GONE
import android.view.View.VISIBLE

import com.wrewolf.thetaleclient.R
import com.wrewolf.thetaleclient.TheTaleClientApplication
import com.wrewolf.thetaleclient.activity.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

import java.net.CookieManager

import javax.inject.Inject

import okhttp3.OkHttpClient
import org.thetale.api.URL

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

        val viewStates = presenter.viewStates
                .observeOn(AndroidSchedulers.mainThread())
        disposables.addAll(
                viewStates
                        .filter { it == LoginState.Initial }
                        .subscribe { requestAppInfo() },

                viewStates
                        .filter { it == LoginState.Loading }
                        .subscribe {
                            progressBar.visibility = VISIBLE
                            thirdPartyConfirm.visibility = GONE
                            content.visibility = GONE
                            error.visibility = GONE
                        },

                viewStates
                        .filter { it == LoginState.Chooser }
                        .subscribe {
                            content.visibility = VISIBLE
                            loginContentStart.visibility = VISIBLE
                            thirdPartyConfirm.visibility = GONE
                            progressBar.visibility = GONE
                            loginPassword.visibility = GONE
                            error.visibility = GONE
                        },

                viewStates
                        .filter { it == LoginState.Credentials }
                        .subscribe {
                            content.visibility = VISIBLE
                            loginPassword.visibility = VISIBLE
                            thirdPartyConfirm.visibility = GONE
                            loginContentStart.visibility = GONE
                            progressBar.visibility = GONE
                            error.visibility = GONE
                        },

                viewStates
                        .filter { it is LoginState.CredentialsError }
                        .map { it as LoginState.CredentialsError }
                        .subscribe {
                            applyCredentialError(it.loginError, it.passwordError)

                            content.visibility = VISIBLE
                            loginPassword.visibility = VISIBLE
                            thirdPartyConfirm.visibility = GONE
                            loginContentStart.visibility = GONE
                            progressBar.visibility = GONE
                            error.visibility = GONE
                        },

                viewStates
                        .filter { it is LoginState.Error }
                        .subscribe {
                            error.visibility = VISIBLE
                            content.visibility = GONE
                            thirdPartyConfirm.visibility = GONE
                            loginPassword.visibility = GONE
                            loginContentStart.visibility = GONE
                            progressBar.visibility = GONE
                        },

                viewStates
                        .filter { it == LoginState.ThirdPartyConfirm }
                        .subscribe {
                            thirdPartyConfirm.visibility = VISIBLE
                            content.visibility = VISIBLE
                            error.visibility = GONE
                            loginPassword.visibility = GONE
                            loginContentStart.visibility = GONE
                            progressBar.visibility = GONE
                        },

                viewStates
                        .filter { it is LoginState.ThirdPartyError }
                        .map { it as LoginState.ThirdPartyError }
                        .subscribe {
                            content.visibility = VISIBLE
                            thirdPartyConfirm.visibility = VISIBLE
                            thirdPartyConfirm.setError(it.error)
                            error.visibility = GONE
                            loginPassword.visibility = GONE
                            loginContentStart.visibility = GONE
                            progressBar.visibility = GONE
                        },

                loginContentStart
                        .loginWithCredentialsEvents()
                        .subscribe {
                            presenter.viewStates.accept(LoginState.Credentials)
                        },

                loginContentStart
                        .loginFromSiteClicks()
                        .flatMap {
                            presenter.thirdPartyLogin(
                                    getString(R.string.app_name),
                                    getString(R.string.app_description),
                                    getString(R.string.app_about))
                                    .subscribeOn(Schedulers.io())
                        }
                        .subscribe(),

                loginContentStart
                        .registerClicks()
                        .subscribe {
                            startRegistration()
                        },

                error
                        .retryClicks()
                        .subscribe { requestAppInfo() },

                loginPassword
                        .loginClicks()
                        .flatMap {
                            presenter.loginWithEmailAndPassword(it.email, it.password).subscribeOn(Schedulers.io())
                        }
                        .subscribe(),

                loginPassword
                        .remindPasswordClicks()
                        .subscribe {
                            openUrl(URL_PASSWORD_REMIND)
                        },

                thirdPartyConfirm
                        .confirmClicks()
                        .flatMap {
                            presenter.thirdPartyAuthStatus()
                                    .subscribeOn(Schedulers.io())
                        }
                        .subscribe()
        )
    }

    private fun requestAppInfo() {
        presenter.checkAppInfo()
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    private fun applyCredentialError(loginError: String?, passwordError: String?) {
        loginPassword.setEmailError(loginError)
        loginPassword.setPasswordError(passwordError)
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
        openUrl(URL_REGISTRATION)
    }

    override fun proceedToGame() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtras(getIntent())

        startActivity(intent)
        finish()
    }

    override fun openThirdPartyAuth(link: String) {
        openUrl("$URL$link")
    }

    private fun openUrl(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    companion object {

        private const val URL_REGISTRATION = "http://the-tale.org/accounts/registration/fast?action=the-tale-client"
        private const val URL_PASSWORD_REMIND = "http://the-tale.org/accounts/profile/reset-password?action=the-tale-client"
    }
}
