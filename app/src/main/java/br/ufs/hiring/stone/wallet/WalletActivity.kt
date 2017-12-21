package br.ufs.hiring.stone.wallet

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import br.ufs.architecture.core.errors.InfrastructureError
import br.ufs.architecture.core.errors.NetworkingIssue
import br.ufs.architecture.core.presentation.behaviors.BehaviorsPresenter
import br.ufs.architecture.core.presentation.errorstate.ErrorStateView
import br.ufs.architecture.core.presentation.loading.LoadingView
import br.ufs.architecture.core.presentation.networking.NetworkingErrorView
import br.ufs.architecture.core.presentation.util.screenProvider
import br.ufs.hiring.stone.R
import br.ufs.hiring.stone.features.wallet.EntryModel
import br.ufs.hiring.stone.features.wallet.WalletScreen
import br.ufs.hiring.stone.widgets.action
import br.ufs.hiring.stone.widgets.colorForActionText
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.with
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.view_error_feedback.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class WalletActivity : AppCompatActivity(),
        LoadingView, NetworkingErrorView, ErrorStateView {

    private val kodein by lazy { LazyKodein(appKodein) }
    private val presentation by kodein.with(this).instance<BehaviorsPresenter>()
    private val screen by screenProvider { kodein.value.instance<WalletScreen>() }

    private val executions by lazy { CompositeDisposable() }

    override fun showLoading() = Action {
        pullToRefresh.apply {
            isRefreshing = true
            isEnabled = true
        }
    }

    override fun hideLoading() = Action {
        pullToRefresh.apply {
            isRefreshing = false
            isEnabled = true
        }
    }

    override fun reportNetworkingError(issue: NetworkingIssue) = Action {
        callToAction(R.string.snacktext_internet_connection, { updateInformation() })
        if (emptyContent()) showFeedback(issue)
    }

    override fun showErrorState(error: InfrastructureError) = Action {
        if (emptyContent()) showFeedback(error)
        callToAction(
                R.string.snacktext_undesired_response,
                { updateInformation() },
                R.string.snackaction_retry
        )
    }

    override fun hideErrorState() = Action {
        clearErrorFeedback()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        setupViews()
    }

    override fun onDestroy() {
        executions.dispose()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        updateInformation()
    }

    private fun setupViews() {
        walletViews.layoutManager = LinearLayoutManager(this)
        pullToRefresh.apply {
            setColorSchemeResources(R.color.colorAccent)
            setOnRefreshListener {
                updateInformation()
            }
        }
    }

    private fun updateInformation() {
        clearErrorFeedback()
        val entriesAdapter = WalletEntriesAdapter()
        val execution = screen
                .updatedInformation()
                .compose(presentation)
                .subscribe(
                        { entriesAdapter.addModel(it as EntryModel) },
                        { Log.e(WalletActivity.TAG, "Failed -> $it") },
                        { walletViews.adapter = entriesAdapter }
                )

        executions.add(execution)
    }

    private fun callToAction(
            callToActionText: Int,
            retryAction: (Any) -> Unit,
            retryText: Int = R.string.snackaction_retry) {

        Snackbar.make(walletRoot, callToActionText, Snackbar.LENGTH_INDEFINITE)
                .action(retryText, retryAction)
                .colorForActionText(R.color.primary)
                .show()
    }

    private fun showFeedback(error: Throwable) {
        feedbackContainer.visibility = View.VISIBLE
        feedbackContainer.setState(error)
    }

    private fun clearErrorFeedback() {
        feedbackContainer.visibility = View.GONE
        feedbackContainer.reset()
    }

    private fun emptyContent() = walletViews.adapter?.let { it.itemCount == 0 } ?: true

    private companion object {
        val TAG = WalletActivity::class.simpleName
    }

}