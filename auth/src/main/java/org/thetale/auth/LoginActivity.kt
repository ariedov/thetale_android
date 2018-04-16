package org.thetale.auth

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import org.thetale.auth.di.LoginComponentProvider
import org.thetale.auth.steps.credentials.LoginCredentialsFragment
import org.thetale.auth.steps.status.CheckStatusFragment
import org.thetale.auth.steps.thirdparty.LoginThirdPartyFragment
import org.thetale.core.AppNavigation
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), LoginNavigation {

    @Inject lateinit var appNavigation: AppNavigation
    @Inject lateinit var navigationProvider: LoginNavigationProvider

    private lateinit var componentProvider: LoginComponentProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        componentProvider = application as LoginComponentProvider
        componentProvider.provideLoginComponent()?.inject(this)

        navigationProvider.navigation = this

        if (savedInstanceState == null) {
            showCheckStatus()
        }
    }

    override fun showCheckStatus() {
        showFragment(CheckStatusFragment.create(), false)
    }

    override fun showCredentials() {
        showFragment(LoginCredentialsFragment.create())
    }

    override fun showThirdParty() {
        showFragment(LoginThirdPartyFragment.create())
    }

    override fun openApp() {
        appNavigation.openApp(this)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        navigationProvider.navigation = null

        if (isFinishing) {
            componentProvider.cleanLoginComponent()
        }
    }
}

private fun FragmentActivity.showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
    supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .apply {
                if (addToBackStack) {
                    addToBackStack(null)
                }
            }
            .commit()
}