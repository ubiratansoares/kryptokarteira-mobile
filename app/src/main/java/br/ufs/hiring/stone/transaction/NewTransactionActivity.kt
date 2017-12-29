package br.ufs.hiring.stone.transaction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import br.ufs.architecture.core.errors.InfrastructureError
import br.ufs.architecture.core.errors.NetworkingIssue
import br.ufs.architecture.core.presentation.behaviors.BehaviorsPresenter
import br.ufs.architecture.core.presentation.errorstate.ErrorStateView
import br.ufs.architecture.core.presentation.loading.LoadingView
import br.ufs.architecture.core.presentation.networking.NetworkingErrorView
import br.ufs.architecture.core.presentation.util.screenProvider
import br.ufs.hiring.stone.R
import br.ufs.hiring.stone.domain.TransactionError.InvalidTransactionParameters
import br.ufs.hiring.stone.domain.TransactionError.TransactionNotAllowed
import br.ufs.hiring.stone.features.transaction.TransactionScreen
import br.ufs.hiring.stone.widgets.action
import br.ufs.hiring.stone.widgets.colorForActionText
import br.ufs.hiring.stone.widgets.toast
import com.afollestad.materialdialogs.MaterialDialog
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.with
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import kotlinx.android.synthetic.main.activity_new_transaction.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class NewTransactionActivity : AppCompatActivity(),
        LoadingView, NetworkingErrorView, ErrorStateView {

    private val kodein by lazy { LazyKodein(appKodein) }
    private val screen by screenProvider { kodein.value.instance<TransactionScreen>() }
    private val presenter by kodein.with(this).instance<BehaviorsPresenter>()

    private val currencyLabel by lazy { intent.getStringExtra(EXTRA_CURRENCY) }
    private val operationType by lazy { intent.getStringExtra(EXTRA_OPERATION) }

    private val subscriptions by lazy { CompositeDisposable() }

    private val dialog by lazy {
        MaterialDialog.Builder(this)
                .cancelable(false)
                .customView(R.layout.view_transaction_submission, true)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_transaction)
        setupViews()
    }

    override fun onDestroy() {
        if (dialog.isShowing) dialog.dismiss()
        subscriptions.clear()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let { if (it.itemId == android.R.id.home) finish() }
        return super.onOptionsItemSelected(item)
    }

    override fun showLoading() = Action {
        dialog.show()
    }

    override fun hideLoading() = Action {
        dialog.dismiss()
    }

    override fun showErrorState(error: InfrastructureError) = Action {
        backToSubmissionState()
        callToAction(
                callToActionText = R.string.snacktext_undesired_response,
                actionText = R.string.snackaction_done
        )
    }

    override fun reportNetworkingError(issue: NetworkingIssue) = Action {
        backToSubmissionState()
        callToAction(
                callToActionText = R.string.snacktext_internet_connection,
                snackAction = { requestTransaction() },
                actionText = R.string.snackaction_retry
        )
    }

    override fun hideErrorState() = Action {
        hideAllOtherViews()
    }

    private fun setupViews() {
        val vm = screen.viewModel(currencyLabel, operationType)
        currencyNameLabel.text = vm.currencyName
        transactionNameLabel.text = vm.operationName

        supportActionBar?.apply {
            title = vm.screenTitle
            setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
            setDisplayHomeAsUpEnabled(true)
        }

        amountInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                toogleButton()
            }
            false
        }

        performTransactionButton.setOnClickListener { requestTransaction() }
    }

    private fun requestTransaction() {
        val amount = amountInput.text.toString().toFloat()

        val execution = screen.performTransaction(amount)
                .compose(presenter)
                .subscribe(
                        {
                            toast("Transação efetuada com sucesso!")
                            done()
                        },
                        {
                            when (it) {
                                is TransactionNotAllowed -> reportNotAllowed()
                                is InvalidTransactionParameters -> reportInconsistency()
                                else -> Log.e(TAG, it.toString())
                            }
                        }
                )

        subscriptions.add(execution)
    }

    private fun reportInconsistency() {
        backToSubmissionState()
        callToAction(callToActionText = R.string.snacktext_invalid_transaction)
    }

    private fun reportNotAllowed() {
        backToSubmissionState()
        callToAction(callToActionText = R.string.snacktext_notallowed_transaction)
    }

    private fun backToSubmissionState() {
        showAllOtherViews()
        dialog.dismiss()
    }

    private fun showAllOtherViews() {
        transactionScreenRoot.visibility = View.VISIBLE
    }

    private fun hideAllOtherViews() {
        transactionScreenRoot.visibility = View.INVISIBLE
    }

    private fun done() = finish()

    private fun toogleButton() {
        val hidden = amountInput.text?.toString().isNullOrEmpty()
        performTransactionButton.apply {
            isEnabled = !hidden
            visibility = if (hidden) View.GONE else View.VISIBLE
        }
    }

    private fun callToAction(
            callToActionText: Int,
            snackAction: (Any) -> Unit = {},
            actionText: Int = R.string.snackaction_allclear) {

        Snackbar.make(transactionScreenRoot, callToActionText, Snackbar.LENGTH_INDEFINITE)
                .action(actionText, snackAction)
                .colorForActionText(R.color.primary)
                .show()
    }

    companion object {

        private val TAG = "NewTransactionActivity"
        private val EXTRA_CURRENCY = "extra.currency"
        private val EXTRA_OPERATION = "extra.operation"

        fun launchIntent(from: Context, currencyLabel: String, operationType: String) =
                Intent(from, NewTransactionActivity::class.java).apply {
                    putExtra(EXTRA_CURRENCY, currencyLabel)
                    putExtra(EXTRA_OPERATION, operationType)
                }
    }
}