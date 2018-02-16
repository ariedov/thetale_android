package com.wrewolf.thetaleclient.login.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.wrewolf.thetaleclient.R
import com.wrewolf.thetaleclient.TheTaleClientApplication
import com.wrewolf.thetaleclient.login.LoginPresenter
import kotlinx.android.synthetic.main.layout_login_content_start.view.*
import javax.inject.Inject

class LoginContentStartView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    @Inject lateinit var presenter: LoginPresenter

    init {
        TheTaleClientApplication
                .getComponentProvider()
                .loginComponent?.inject(this)

        orientation = VERTICAL

        LayoutInflater.from(context).inflate(R.layout.layout_login_content_start, this)

        loginFromSite.setOnClickListener { presenter.loginFromSite() }
        loginWithCredentials.setOnClickListener { presenter.chooseLoginWithCredentials() }
        registerAccount.setOnClickListener { presenter.registerAccount() }
    }
}