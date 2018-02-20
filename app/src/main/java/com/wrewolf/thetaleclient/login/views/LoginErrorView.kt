package com.wrewolf.thetaleclient.login.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.jakewharton.rxbinding2.view.RxView
import com.wrewolf.thetaleclient.R
import io.reactivex.Observable
import kotlinx.android.synthetic.main.layout_login_error.view.*

class LoginErrorView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = VERTICAL

        LayoutInflater.from(getContext()).inflate(R.layout.layout_login_error, this)
    }

    fun setErrorText(error: String) {
        errorText.text = error
    }

    fun retryClicks(): Observable<Any> {
        return RxView.clicks(retry)
    }
}