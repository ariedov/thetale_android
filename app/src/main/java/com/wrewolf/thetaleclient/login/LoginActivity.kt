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
import kotlinx.android.synthetic.main.activity_login.*

import java.net.CookieManager

import javax.inject.Inject

import okhttp3.OkHttpClient
import org.thetale.api.URL

class LoginActivity : AppCompatActivity(), LoginNavigation, LoginView {

    @Inject lateinit var presenter: LoginPresenter
    @Inject lateinit var client: OkHttpClient
    @Inject lateinit var cookieManager: CookieManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TheTaleClientApplication.getComponentProvider()
                .loginComponent!!
                .inject(this)

        presenter.navigator = this
        presenter.view = this

        setContentView(R.layout.activity_login)
    }

    override fun onStart() {
        super.onStart()

        presenter.start()

//        val viewStates = presenter.viewStates
//                .observeOn(AndroidSchedulers.mainThread())
//        disposables = CompositeDisposable()
//        disposables.addAll(
//                viewStates
//                        .filter { it == LoginState.Initial }
//                        .observeOn(Schedulers.io())
//                        .flatMap { presenter.checkAppInfo() }
//                        .subscribe(),
//
//                viewStates
//                        .filter { it == LoginState.Loading }
//                        .subscribe { showLoading() },
//
//                viewStates
//                        .filter { it == LoginState.Chooser }
//                        .subscribe { showChooser() },
//
//                viewStates
//                        .filter { it == LoginState.Credentials }
//                        .subscribe { showEmailPassword() },
//
//                viewStates
//                        .filter { it is LoginState.CredentialsError }
//                        .map { it as LoginState.CredentialsError }
//                        .subscribe { showLoginError(it.login, it.password, it.loginError, it.passwordError) },
//
//                viewStates
//                        .filter { it is LoginState.Error }
//                        .subscribe { showInitError() },
//
//                viewStates
//                        .filter { it == LoginState.ThirdPartyConfirm }
//                        .subscribe { showThirdParty() },
//                viewStates
//                        .filter { it == LoginState.ThirdPartyStatusError }
//                        .subscribe { showThirdPartyStatusError() },
//
//                viewStates
//                        .filter { it == LoginState.ThirdPartyError }
//                        .subscribe { showThirdPartyError() })
//
//        disposables.addAll(
//                loginContentStart
//                        .loginWithCredentialsEvents()
//                        .subscribe {
//                            presenter.viewStates.accept(LoginState.Credentials)
//                        },
//
//                loginContentStart
//                        .loginFromSiteClicks()
//                        .observeOn(Schedulers.io())
//                        .flatMap {
//                            presenter.thirdPartyLogin(
//                                    getString(R.string.app_name),
//                                    getString(R.string.app_description),
//                                    getString(R.string.app_about))
//                        }
//                        .subscribe(),
//
//                loginContentStart
//                        .registerClicks()
//                        .subscribe {
//                            startRegistration()
//                        })
//
//        disposables.add(
//                error
//                        .retryClicks()
//                        .observeOn(Schedulers.io())
//                        .flatMap { presenter.checkAppInfo() }
//                        .subscribe())
//
//        disposables.add(
//                thirdPartyConfirm
//                        .confirmClicks()
//                        .observeOn(Schedulers.io())
//                        .flatMap { presenter.thirdPartyAuthStatus() }
//                        .subscribe())
//
//        disposables.addAll(
//                loginPassword
//                        .loginClicks()
//                        .observeOn(Schedulers.io())
//                        .flatMap { presenter.loginWithEmailAndPassword(it.email, it.password) }
//                        .subscribe(),
//
//                loginPassword
//                        .remindPasswordClicks()
//                        .subscribe { openUrl(URL_PASSWORD_REMIND) })
    }

    override fun showLoading() {
        progressBar.visibility = VISIBLE
        content.visibility = GONE
        error.visibility = GONE
    }

    override fun showChooser() {
        content.visibility = VISIBLE
        loginContentStart.visibility = VISIBLE
        thirdPartyConfirm.visibility = GONE
        progressBar.visibility = GONE
        loginPassword.visibility = GONE
        error.visibility = GONE
    }

    override fun showEmailPassword() {
        content.visibility = VISIBLE
        loginPassword.visibility = VISIBLE
        thirdPartyConfirm.visibility = GONE
        loginContentStart.visibility = GONE
        progressBar.visibility = GONE
        error.visibility = GONE
    }

    override fun showEmailPassword(email: String, password: String) {
        loginPassword.setEmail(email)
        loginPassword.setPassword(password)

        showEmailPassword()
    }

    override fun showLoginError(email: String, password: String, emailError: String?, passwordError: String?) {
        loginPassword.setEmailError(emailError)
        loginPassword.setPasswordError(passwordError)

        showEmailPassword()
    }

    override fun showInitError() {
        error.visibility = VISIBLE
        content.visibility = GONE
        thirdPartyConfirm.visibility = GONE
        loginPassword.visibility = GONE
        loginContentStart.visibility = GONE
        progressBar.visibility = GONE
    }

    override fun showThirdParty() {
        thirdPartyConfirm.visibility = VISIBLE
        content.visibility = VISIBLE
        error.visibility = GONE
        loginPassword.visibility = GONE
        loginContentStart.visibility = GONE
        progressBar.visibility = GONE
    }

    override fun showThirdPartyError() {
        error.visibility = VISIBLE
        content.visibility = GONE
        thirdPartyConfirm.visibility = GONE
        loginPassword.visibility = GONE
        loginContentStart.visibility = GONE
        progressBar.visibility = GONE
    }

    override fun showThirdPartyStatusError() {
        content.visibility = VISIBLE
        thirdPartyConfirm.visibility = VISIBLE
        thirdPartyConfirm.setError(getString(R.string.error_third_party_login))
        error.visibility = GONE
        loginPassword.visibility = GONE
        loginContentStart.visibility = GONE
        progressBar.visibility = GONE
    }

    override fun onStop() {
        super.onStop()
//        disposables.dispose()
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
