package com.wrewolf.thetaleclient.login

sealed class LoginState {
    object Initial: LoginState()
    object Loading: LoginState()
    object Chooser: LoginState()
    object Credentials: LoginState()
    object ThirdPartyConfirm: LoginState()
    data class CredentialsError(val login: String, val password: String,
                                val loginError: String? = null,
                                val passwordError: String? = null): LoginState()
    data class ThirdPartyError(val error: String? = null): LoginState()
    data class Error(val error: String? = null): LoginState()
}