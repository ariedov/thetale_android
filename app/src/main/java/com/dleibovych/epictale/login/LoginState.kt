package com.dleibovych.epictale.login

sealed class LoginState {
    object Initial: LoginState()
    object Loading: LoginState()
    data class Error(val error: String? = null): LoginState()

    object Chooser: LoginState()

    object Credentials: LoginState()
    data class CredentialsError(val login: String, val password: String,
                                val loginError: String? = null,
                                val passwordError: String? = null): LoginState()

    object ThirdPartyConfirm: LoginState()
    object ThirdPartyError: LoginState()
    object ThirdPartyStatusError: LoginState()
}