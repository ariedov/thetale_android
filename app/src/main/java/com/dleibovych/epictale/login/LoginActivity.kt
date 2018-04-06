package com.dleibovych.epictale.login

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity

import com.dleibovych.epictale.R
import com.dleibovych.epictale.TheTaleClientApplication
import com.dleibovych.epictale.activity.MainActivity
import com.dleibovych.epictale.login.steps.credentials.LoginCredentialsFragment
import com.dleibovych.epictale.login.steps.status.CheckStatusFragment
import com.dleibovych.epictale.login.steps.thirdparty.LoginThirdPartyFragment

class LoginActivity : AppCompatActivity(), LoginNavigation {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        TheTaleClientApplication.getComponentProvider()
                .createLoginComponent(this)

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