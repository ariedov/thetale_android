package com.wrewolf.thetaleclient.login

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity

import com.wrewolf.thetaleclient.R
import com.wrewolf.thetaleclient.TheTaleClientApplication
import com.wrewolf.thetaleclient.activity.MainActivity
import com.wrewolf.thetaleclient.login.steps.chooser.LoginChooserFragment
import com.wrewolf.thetaleclient.login.steps.credentials.LoginCredentialsFragment
import com.wrewolf.thetaleclient.login.steps.status.CheckStatusFragment
import com.wrewolf.thetaleclient.login.steps.thirdparty.LoginThirdPartyFragment

class LoginActivity : AppCompatActivity(), LoginNavigation {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        TheTaleClientApplication.getComponentProvider()
                .createLoginComponent(this)

        showCheckStatus()
    }

    override fun showCheckStatus() {
        showFragment(CheckStatusFragment.create())
    }

    override fun showChooser() {
        showFragment(LoginChooserFragment.create())
    }

    override fun showCredentials() {
        showFragment(LoginCredentialsFragment.create())
    }

    override fun showThirdParty(link: String) {
        showFragment(LoginThirdPartyFragment.create(link))
    }

    override fun openApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtras(getIntent())

        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) {
            TheTaleClientApplication.getComponentProvider().loginComponent = null
        }
    }

    companion object {

        private const val URL_REGISTRATION = "http://the-tale.org/accounts/registration/fast?action=the-tale-client"
        private const val URL_PASSWORD_REMIND = "http://the-tale.org/accounts/profile/reset-password?action=the-tale-client"
    }
}

private fun FragmentActivity.showFragment(fragment: Fragment) {
    supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
}