package br.ufs.hiring.stone.transaction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import br.ufs.architecture.core.presentation.util.screenProvider
import br.ufs.hiring.stone.R
import br.ufs.hiring.stone.features.transaction.TransactionScreen
import br.ufs.hiring.stone.widgets.toast
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import kotlinx.android.synthetic.main.activity_new_transaction.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class NewTransactionActivity : AppCompatActivity() {

    private val kodein by lazy { LazyKodein(appKodein) }
    private val screen by screenProvider { kodein.value.instance<TransactionScreen>() }


    private val currencyLabel by lazy { intent.getStringExtra(EXTRA_CURRENCY) }
    private val operationType by lazy { intent.getStringExtra(EXTRA_OPERATION) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_transaction)
        setupViews()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let { if (it.itemId == android.R.id.home) finish() }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViews() {
        val vm = screen.viewModel(currencyLabel, operationType)

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

        currencyNameLabel.text = vm.currencyName
        transactionNameLabel.text = vm.operationName

        performTransactionButton.setOnClickListener {
            val amount = amountInput.text.toString().toFloat()
            screen.performTransaction(amount)
                    .subscribe(
                            {
                                toast("Sucesso!")
                                finish()
                            },
                            { Log.e(TAG, "Failed -> $it") }
                    )
        }
    }

    private fun toogleButton() {
        val hidden = amountInput.text?.toString().isNullOrEmpty()
        performTransactionButton.apply {
            isEnabled = !hidden
            visibility = if (hidden) View.GONE else View.VISIBLE
        }
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