package com.wrewolf.thetaleclient.login.steps.chooser

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.wrewolf.thetaleclient.R
import com.wrewolf.thetaleclient.TheTaleClientApplication
import com.wrewolf.thetaleclient.login.steps.credentials.LoginCredentialsFragment
import com.wrewolf.thetaleclient.login.steps.thirdparty.LoginThirdPartyFragment
import kotlinx.android.synthetic.main.fragment_login_chooser.*
import javax.inject.Inject

class LoginChooserFragment: Fragment(), ChooserView, ChooserNavigator {

    @Inject lateinit var presenter: ChooserPresenter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        TheTaleClientApplication.getComponentProvider()
                .loginComponent!!
                .inject(this)

        presenter.view = this
        presenter.navigator = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginContentStart.onLoginFromSiteClick(View.OnClickListener {
            presenter.thirdPartyLogin(
                    getString(R.string.app_name),
                    getString(R.string.app_description),
                    getString(R.string.app_about))
        })

        loginContentStart.onLoginWithCredentials(View.OnClickListener { presenter.credentialsLogin() })
    }

    override fun onStart() {
        super.onStart()

        presenter.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (activity!!.isFinishing) {
            presenter.dispose()
        }
    }

    override fun openThirdPartyLogin(url: String) {
        fragmentManager!!
                .beginTransaction()
                .replace(R.id.container, LoginThirdPartyFragment.create(url))
                .commit()
    }

    override fun openCredentialsLogin() {
        fragmentManager!!
                .beginTransaction()
                .replace(R.id.container, LoginCredentialsFragment.create())
                .commit()
    }

    override fun showProgress() {
        progress.visibility = VISIBLE
        loginContentStart.visibility = GONE
    }

    override fun hideProgress() {
        progress.visibility = GONE
        loginContentStart.visibility = VISIBLE
    }

    companion object {

        fun create(): LoginChooserFragment = LoginChooserFragment()
    }
}