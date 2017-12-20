package br.ufs.hiring.stone.features.wallet

import br.ufs.hiring.stone.features.wallet.EntryType.*
import java.text.NumberFormat
import java.util.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */


object EntriesFromWalletInformation {

    private val currencyFormatter by lazy {
        NumberFormat.getNumberInstance(Locale.GERMAN).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }

    operator fun invoke(info: HomeInformation): List<EntryModel> {
        val entries = mutableListOf<EntryModel>()

        entries.addAll(entriesForSavings(info.savings))
        entries.addAll(entriesForBrokings(info.brokings))
        entries.addAll(entriesForTransactions(info.transactions))

        return entries
    }


    private fun entriesForSavings(savings: List<Saving>): List<EntryModel> {
        val entries = mutableListOf<EntryModel>()

        entries += Headline(type = HeadlineType.Block, headline = "Seus investimentos")
        entries += CardHeader()

        savings.forEach {
            entries += Investiment(
                    formattedName = "Em ${it.currency.name}",
                    formattedValue = formattedSavingOrNone(it)
            )
        }

        entries += CardFooter()

        return entries
    }

    private fun entriesForBrokings(brokings: List<BrokingInformation>): List<EntryModel> {
        val entries = mutableListOf<EntryModel>()

        entries += Headline(type = HeadlineType.Block, headline = "Cotações Atualizadas")

        brokings.forEach {
            entries += CardHeader()
            entries += Headline(type = HeadlineType.Trade, headline = it.currency.name)
            entries += TradeValue(
                    "Preço de compra",
                    "R$ ${formattedCurrency(it.buyPrice)}"
            )

            entries += TradeValue(
                    "Preço de venda",
                    "R$ ${formattedCurrency(it.sellPrice)}"
            )

            entries += IntraCardSpace()
            entries += HorizontalLine()
            entries += CallToAction()
            entries += CardFooter()
            entries += BetweenCardsSpace()
        }


        return entries

    }

    private fun entriesForTransactions(transactions: List<Transaction>): List<EntryModel> {

        if (transactions.isEmpty()) return emptyList()

        val entries = mutableListOf<EntryModel>()

        entries += Headline(type = HeadlineType.Block, headline = "Suas movimentações")
        entries += CardHeader()

        when (transactions.size) {
            1 -> {
                val unique = transactions.first()
                entries += transacationEntry(unique, Standalone)
            }

            else -> {
                val header = transactions.first()
                val footer = transactions.last()

                entries += transacationEntry(transactions.first(), Header)

                transactions.forEach { transaction ->
                    if (transaction != header || transaction != footer) {
                        entries += transacationEntry(transaction, Middle)
                    }
                }

                entries += transacationEntry(footer, Footer)
            }
        }

        entries += CardFooter()
        return entries
    }

    private fun transacationEntry(
            transaction: Transaction,
            target: EntryType): TransactionEntry {

        return with(transaction) {
            TransactionEntry(
                    type = target,
                    currency = currency.name,
                    transcationType = formattedOperation(type),
                    formattedDate = "em 26/02/2017",
                    formattedTotal = "$amount ${currency.label.toUpperCase()}"
            )
        }
    }

    private fun formattedOperation(type: TransactionType): String {
        return when (type) {
            is TransactionType.Sell -> "VENDA"
            is TransactionType.Buy -> "COMPRA"
        }
    }

    private fun formattedSavingOrNone(it: Saving): String =
            if (it.amount == 0.0f) "Sem investimentos"
            else "${formattedCurrency(it.amount)} ${it.currency.label.toUpperCase()}"

    private fun formattedCurrency(number: Float): String {
        return currencyFormatter.format(number)
    }
}