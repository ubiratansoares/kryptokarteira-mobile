package br.ufs.hiring.stone.features.transaction

import br.ufs.architecture.core.presentation.util.Screen
import br.ufs.hiring.stone.domain.Currency
import br.ufs.hiring.stone.domain.NewTrasaction
import br.ufs.hiring.stone.domain.Transaction
import br.ufs.hiring.stone.domain.TransactionType
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import kotlin.properties.Delegates

/**
 *
 * Created by @ubiratanfsoares
 *
 */
class TransactionScreen(
        private val usecase: NewTrasaction,
        private val uiScheduler: Scheduler = Schedulers.trampoline()) : Screen() {

    private var actualCurrency by Delegates.notNull<Currency>()
    private var actualTransaction by Delegates.notNull<TransactionType>()

    fun viewModel(currencyLabel: String, operationType: String): TransactionViewModel {

        actualCurrency = Currency.From(currencyLabel)
        actualTransaction = TransactionType.From(operationType)

        return TransactionViewModel(
                screenTitle = "Nova transação",
                currencyName = actualCurrency.name.toUpperCase(),
                operationName = formatTransactionName().toUpperCase()
        )
    }

    private fun formatTransactionName(): String {
        return when (actualTransaction) {
            is TransactionType.Buy -> "Compra"
            is TransactionType.Sell -> "Venda"
        }
    }

    fun performTransaction(amount: Float): Completable {
        val transaction = validTransaction(amount)
        return usecase.perform(transaction).observeOn(uiScheduler)
    }

    private fun validTransaction(desiredQuantity: Float) = Transaction(
            currency = actualCurrency,
            type = actualTransaction,
            amount = desiredQuantity
    )
}