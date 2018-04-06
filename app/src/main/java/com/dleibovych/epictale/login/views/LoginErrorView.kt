package com.dleibovych.epictale.login.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.dleibovych.epictale.R
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

    fun onRetryClick(listener: OnClickListener) {
        retry.setOnClickListener(listener)
    }
}