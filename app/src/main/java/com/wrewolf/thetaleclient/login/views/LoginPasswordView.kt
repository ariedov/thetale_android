package com.wrewolf.thetaleclient.login.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.jakewharton.rxbinding2.view.RxView
import com.wrewolf.thetaleclient.R
import io.reactivex.Observable
import kotlinx.android.synthetic.main.layout_login_password.view.*

class LoginPasswordView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = VERTICAL

        LayoutInflater.from(context).inflate(R.layout.layout_login_password, this)
    }

    fun loginClicks(): Observable<LoginEvent> {
        return RxView.clicks(actionLogin)
                .map { LoginEvent(
                        emailLayout.editText!!.text.toString(),
                        passwordLayout.editText!!.text.toString())
                }
    }

    fun remindPasswordClicks(): Observable<Any> {
        return RxView.clicks(passwordRemind)
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