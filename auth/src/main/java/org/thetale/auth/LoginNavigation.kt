package org.thetale.auth

interface LoginNavigation {

    fun showCheckStatus()

    fun showCredentials()

    fun showThirdParty()

    fun openApp()
}