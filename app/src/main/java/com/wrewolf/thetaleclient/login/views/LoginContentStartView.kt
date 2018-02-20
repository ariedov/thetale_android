package com.wrewolf.thetaleclient.login.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.jakewharton.rxbinding2.view.RxView
import com.wrewolf.thetaleclient.R
import io.reactivex.Observable
import kotlinx.android.synthetic.main.layout_login_content_start.view.*

class LoginContentStartView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = VERTICAL

        LayoutInflater.from(context).inflate(R.layout.layout_login_content_start, this)
    }

    fun loginFromSiteClicks(): Observable<Any> {
        return RxView
                .clicks(loginFromSite)
    }

    fun loginWithCredentialsEvents(): Observable<Any> {
        return RxView
                .clicks(loginWithCredentials)
    }

    fun registerClicks(): Observable<Any> {
        return RxView
                .clicks(registerAccount)
    }
}