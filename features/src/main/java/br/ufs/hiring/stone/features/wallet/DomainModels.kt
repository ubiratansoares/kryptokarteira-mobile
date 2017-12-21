package br.ufs.hiring.stone.features.wallet

import java.util.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */

sealed class Currency(val label: String, val name: String) {
    object Brita : Currency("bta", "Brita")
    object Bitcoin : Currency("btc", "Bitcoin")
    object Real : Currency("blr", "Real")

    companion object From {
        operator fun invoke(label: String) = when (label) {
            "bta" -> Currency.Brita
            "btc" -> Currency.Bitcoin
            "blr" -> Currency.Real
            else -> throw IllegalArgumentException("Unknow label")
        }
    }
}

data class BrokingInformation(
        val currency: Currency,
        val sellPrice: Float,
        val buyPrice: Float
)

data class Saving(val currency: Currency, val amount: Float)

sealed class TransactionType {
    object Sell : TransactionType()
    object Buy : TransactionType()

    companion object From {
        operator fun invoke(type: String): TransactionType = when (type) {
            "buy" -> TransactionType.Buy
            "sell" -> TransactionType.Sell
            else -> throw IllegalArgumentException("Unknow transaction type")
        }
    }
}

data class Transaction(
        val type: TransactionType,
        val currency: Currency,
        val timestamp: Date,
        val amount: Float
)

data class HomeInformation(
        val brokings: List<BrokingInformation>,
        val savings: List<Saving>,
        val transactions: List<Transaction>
)

