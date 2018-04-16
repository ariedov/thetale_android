package org.thetale.auth.steps.status

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_check_status.*
import org.thetale.auth.R
import org.thetale.auth.di.LoginComponentProvider
import javax.inject.Inject

class CheckStatusFragment : Fragment(), CheckStatusView {

    @Inject
    lateinit var presenter: CheckStatusPresenter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity?.application as LoginComponentProvider)
                .provideLoginComponent()
                ?.inject(this)

        presenter.view = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_check_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        error.onRetryClick(View.OnClickListener {
            presenter.retry()
        })
    }

    override fun onStart() {
        super.onStart()

        presenter.start()
    }

    override fun onStop() {
        super.onStop()

        presenter.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.view = null
    }

    override fun showLoading() {
        progress.visibility = VISIBLE
        error.visibility = GONE
    }

    override fun hideLoading() {
        progress.visibility = GONE
        error.visibility = GONE
    }

    override fun showError() {
        progress.visibility = GONE
        error.visibility = VISIBLE
        error.setErrorText(getString(R.string.common_error))
    }

    override fun onDestroy() {
        super.onDestroy()

        if (activity!!.isFinishing) {
            presenter.dispose()
        }
    }

    companion object {
        fun create(): CheckStatusFragment = CheckStatusFragment()
    }
}