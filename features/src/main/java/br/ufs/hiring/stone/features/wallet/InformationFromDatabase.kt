package br.ufs.hiring.stone.features.wallet

import br.ufs.hiring.stone.data.database.Broking
import br.ufs.hiring.stone.data.database.Saving
import br.ufs.hiring.stone.data.database.Transaction
import br.ufs.hiring.stone.domain.BrokingInformation
import br.ufs.hiring.stone.domain.Currency
import br.ufs.hiring.stone.domain.HomeInformation
import br.ufs.hiring.stone.domain.TransactionType
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
                    currency = Currency(it.label),
                    sellPrice = it.sellPrice,
                    buyPrice = it.buyPrice
            )
        }

        val savings = savingTuples.map {
            br.ufs.hiring.stone.domain.Saving(
                    currency = Currency(it.label),
                    amount = it.amount
            )
        }

        val transactions = transactionTuples.map {
            br.ufs.hiring.stone.domain.Transaction(
                    currency = Currency(it.currency),
                    amount = it.amount,
                    type = TransactionType(it.type),
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