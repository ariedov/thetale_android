package org.thetale.auth.steps.status

interface CheckStatusView {

    fun showLoading()

    fun hideLoading()

    fun showError()
}