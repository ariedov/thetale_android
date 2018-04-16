package org.thetale.auth.di

import dagger.Subcomponent
import org.thetale.auth.LoginActivity
import org.thetale.auth.steps.credentials.LoginCredentialsFragment
import org.thetale.auth.steps.status.CheckStatusFragment
import org.thetale.auth.steps.thirdparty.LoginThirdPartyFragment

@LoginScope
@Subcomponent(modules = [LoginModule::class])
interface LoginComponent {

    fun inject(target: LoginActivity)

    fun inject(target: LoginThirdPartyFragment)

    fun inject(target: CheckStatusFragment)

    fun inject(target: LoginCredentialsFragment)
}