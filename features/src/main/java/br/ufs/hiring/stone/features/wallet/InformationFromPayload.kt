package br.ufs.hiring.stone.features.wallet

import br.ufs.hiring.stone.data.webservice.models.HomePayload
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */

object InformationFromPayload {

    operator fun invoke(payload: HomePayload): HomeInformation {

        val currencies = payload.currencies.map { currencyFrom(it.label) }
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
                    currency = currencyFrom(it.currency),
                    amount = it.amount,
                    type = typeFrom(it.type),
                    timestamp = convertToDateTime(it.timestamp)
            )
        }

        return HomeInformation(brokings, savings, transactions)
    }

    private fun convertToDateTime(timestamp: String): Date {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        return formatter.parse(timestamp)
    }

    private fun typeFrom(type: String): TransactionType = when (type) {
        "buy" -> TransactionType.Buy
        "sell" -> TransactionType.Sell
        else -> throw IllegalArgumentException("Unknow transaction type")
    }

    private fun currencyFrom(label: String) = when (label) {
        "bta" -> Currency.Brita
        "btc" -> Currency.Bitcoin
        "blr" -> Currency.Real
        else -> throw IllegalArgumentException("Unknow label")
    }

}