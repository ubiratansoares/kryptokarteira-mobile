package br.ufs.hiring.stone.features.tests.fixtures

import br.ufs.hiring.stone.data.database.BrokingRow
import br.ufs.hiring.stone.data.database.SavingRow
import br.ufs.hiring.stone.data.database.TransactionRow
import br.ufs.hiring.stone.features.wallet.ParseTuples
import java.util.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */

object Fixtures {

    val homeInformation by lazy {
        ParseTuples(brokingRows, savingRows, transactionsRows)
    }

    val owner by lazy { UUID.randomUUID().toString() }

    val brokingRows by lazy {
        listOf(
                BrokingRow(
                        updatedAt = 123456,
                        label = "bta",
                        buyPrice = 3.14f,
                        sellPrice = 3.13f
                )
        )
    }

    val savingRows by lazy {
        listOf(
                SavingRow(
                        ownerId = owner,
                        label = "blr",
                        amount = 50000f
                ),
                SavingRow(
                        ownerId = owner,
                        label = "btc",
                        amount = 1f
                ),
                SavingRow(
                        ownerId = owner,
                        label = "bta",
                        amount = 1000f
                )
        )

    }

    val transactionsRows by lazy {
        listOf(
                TransactionRow(
                        ownerId = owner,
                        amount = 1.0f,
                        type = "buy",
                        timestamp = "2017-12-10T03:00:00.000",
                        currency = "btc"
                ),
                TransactionRow(
                        ownerId = owner,
                        amount = 110f,
                        type = "buy",
                        timestamp = "2017-12-14T03:30:00.000",
                        currency = "bta"
                ),
                TransactionRow(
                        ownerId = owner,
                        amount = 100f,
                        type = "sell",
                        timestamp = "2017-12-14T04:00:00.000",
                        currency = "bta"
                )
        )
    }

}