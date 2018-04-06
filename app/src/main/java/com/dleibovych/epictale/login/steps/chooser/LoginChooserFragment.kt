package com.dleibovych.epictale.login.steps.chooser

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.dleibovych.epictale.R
import com.dleibovych.epictale.TheTaleClientApplication
import kotlinx.android.synthetic.main.fragment_login_chooser.*
import javax.inject.Inject

class LoginChooserFragment: Fragment(), ChooserView {

    @Inject lateinit var presenter: ChooserPresenter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        TheTaleClientApplication.getComponentProvider()
                .loginComponent!!
                .inject(this)

        presenter.view = this
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

    override fun onStop() {
        super.onStop()

        presenter.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (activity!!.isFinishing) {
            presenter.dispose()
        }
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