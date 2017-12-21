package br.ufs.hiring.stone.features.wallet

import br.ufs.hiring.stone.data.webservice.models.HomePayload
import br.ufs.hiring.stone.domain.*
import br.ufs.hiring.stone.domain.Currency
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */

object InformationFromPayload {

    operator fun invoke(payload: HomePayload): HomeInformation {

        val currencies = payload.currencies.map { Currency.From(it.label) }

        val brokings = payload.broking.map {
            BrokingInformation(
                    currency = currencies.first { currency -> currency.label == it.label },
                    sellPrice = it.sellPrice,
                    buyPrice = it.buyPrice
            )
        }

        val savings = payload.wallet.savings.map {
            Saving(
                    currency = currencies.first { currency -> currency.label == it.label },
                    amount = it.amount
            )
        }

        val transactions = payload.wallet.transactions.map {
            Transaction(
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