package com.wrewolf.thetaleclient.login.steps.credentials

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.wrewolf.thetaleclient.R
import kotlinx.android.synthetic.main.layout_login_password.view.*

class LoginPasswordView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = VERTICAL

        LayoutInflater.from(context).inflate(R.layout.layout_login_password, this)
    }

    fun onLoginClick(callback: (LoginEvent) -> Unit) {
        actionLogin.setOnClickListener {
            callback.invoke(LoginEvent(
                    emailLayout.editText?.text.toString(), passwordLayout.editText?.text.toString()))
        }
    }

    fun onRemindPasswordClick(listener: OnClickListener) {
        passwordRemind.setOnClickListener(listener)
    }

    fun setEmail(email: String) {
        emailLayout.editText?.setText(email)
    }

    fun setPassword(password: String) {
        passwordLayout.editText?.setText(password)
    }

    fun setEmailError(error: String?) {
        emailLayout.error = error
    }

    fun setPasswordError(error: String?) {
        passwordLayout.error = error
    }
}

data class LoginEvent(val email: String, val password: String)