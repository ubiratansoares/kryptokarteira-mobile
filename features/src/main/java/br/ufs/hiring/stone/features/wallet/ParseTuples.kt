package br.ufs.hiring.stone.features.wallet

import br.ufs.architecture.core.errors.InfrastructureError.LocalDatabaseAccessError
import br.ufs.hiring.stone.data.database.BrokingRow
import br.ufs.hiring.stone.data.database.SavingRow
import br.ufs.hiring.stone.data.database.TransactionRow
import br.ufs.hiring.stone.domain.*
import br.ufs.hiring.stone.features.util.ISO8601

/**
 *
 * Created by @ubiratanfsoares
 *
 */
object ParseTuples {

    operator fun invoke(brokingRows: List<BrokingRow>,
                        savingRows: List<SavingRow>,
                        transactionRows: List<TransactionRow>): HomeInformation {
        try {
            val brokings = brokingRows.map {
                BrokingInformation(
                        currency = Currency(it.label),
                        sellPrice = it.sellPrice,
                        buyPrice = it.buyPrice
                )
            }

            val savings = savingRows.map {
                Saving(
                        currency = Currency(it.label),
                        amount = it.amount
                )
            }

            val transactions = transactionRows.map {
                Transaction(
                        currency = Currency(it.currency),
                        amount = it.amount,
                        type = TransactionType(it.type),
                        timestamp = ISO8601.dateFromUnzonedString(it.timestamp)
                )
            }

            return HomeInformation(brokings, savings, transactions)
        } catch (error: Throwable) {
            throw LocalDatabaseAccessError
        }

    }

}