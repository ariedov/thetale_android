package com.wrewolf.thetaleclient.login.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.wrewolf.thetaleclient.R
import kotlinx.android.synthetic.main.layout_login_content_start.view.*

class LoginContentStartView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = VERTICAL

        LayoutInflater.from(context).inflate(R.layout.layout_login_content_start, this)
    }

    fun onLoginFromSiteClick(listener: OnClickListener) {
        loginFromSite.setOnClickListener(listener)
    }

    fun onLoginWithCredentials(listener: OnClickListener) {
        loginWithCredentials.setOnClickListener(listener)
    }

    fun registerClicks(listener: OnClickListener) {
        registerAccount.setOnClickListener(listener)
    }
}