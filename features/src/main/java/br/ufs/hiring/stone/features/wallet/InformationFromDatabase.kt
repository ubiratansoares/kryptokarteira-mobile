package br.ufs.hiring.stone.features.wallet

import br.ufs.hiring.stone.data.database.Broking
import br.ufs.hiring.stone.data.database.Saving
import br.ufs.hiring.stone.data.database.Transaction
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */
object InformationFromDatabase {

    operator fun invoke(brokingTuples: List<Broking>,
                        savingTuples: List<Saving>,
                        transactionTuples: List<Transaction>): HomeInformation {


        val brokings = brokingTuples.map {
            BrokingInformation(
                    currency = Currency.From(it.label),
                    sellPrice = it.sellPrice,
                    buyPrice = it.buyPrice
            )
        }

        val savings = savingTuples.map {
            Saving(
                    currency = Currency.From(it.label),
                    amount = it.amount
            )
        }

        val transactions = transactionTuples.map {
            Transaction(
                    currency = Currency.From(it.currency),
                    amount = it.amount,
                    type = TransactionType.From(it.type),
                    timestamp = convertToDateTime(it.timestamp)
            )
        }

        return HomeInformation(brokings, savings, transactions)
    }

    private fun convertToDateTime(timestamp: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        return formatter.parse(timestamp)
    }


}