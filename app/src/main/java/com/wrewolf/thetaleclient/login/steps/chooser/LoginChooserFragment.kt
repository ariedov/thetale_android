package com.wrewolf.thetaleclient.login.steps.chooser

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wrewolf.thetaleclient.R
import com.wrewolf.thetaleclient.TheTaleClientApplication
import javax.inject.Inject

class LoginChooserFragment: Fragment(), ChooserView, ChooserNavigator {

    @Inject lateinit var presenter: ChooserPresenter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        TheTaleClientApplication.getComponentProvider()
                .loginComponent!!
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_chooser, container, false)
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
    }

    override fun openCredentialsLogin() {
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }
}