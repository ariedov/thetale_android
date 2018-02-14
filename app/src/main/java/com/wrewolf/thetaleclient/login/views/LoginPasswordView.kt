package com.wrewolf.thetaleclient.login.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.wrewolf.thetaleclient.R
import com.wrewolf.thetaleclient.TheTaleClientApplication
import com.wrewolf.thetaleclient.login.LoginPresenter
import kotlinx.android.synthetic.main.layout_login_password.view.*
import javax.inject.Inject

class LoginPasswordView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    @Inject lateinit var presenter: LoginPresenter

    init {
        (context.applicationContext as TheTaleClientApplication)
                .loginComponent()
                .inject(this)

        orientation = VERTICAL

        LayoutInflater.from(context).inflate(R.layout.layout_login_password, this)

        actionLogin.setOnClickListener {
            presenter.loginWithEmailAndPassword(
                    email.text.toString(),
                    password.text.toString())
        }

        passwordRemind.setOnClickListener { presenter.remindPassword() }
    }
}