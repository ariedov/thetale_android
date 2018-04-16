package org.thetale.auth.steps.thirdparty

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.layout_login_confirm_third_party.view.*
import org.thetale.auth.R

class LoginConfirmThirdPartyView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = VERTICAL
        LayoutInflater.from(context)
                .inflate(R.layout.layout_login_confirm_third_party, this)
    }

    fun onConfirmClick(listener: OnClickListener) {
        confirmLogin.setOnClickListener(listener)
    }

    fun onTryAgainClick(listener: OnClickListener) {
        tryAgain.setOnClickListener(listener)
    }

    fun setError(error: String?) {
        thirdPartyError.text = error
    }
}