package br.ufs.hiring.stone.features.tests.wallet

import br.ufs.architecture.core.errors.InfrastructureError.LocalDatabaseAccessError
import br.ufs.hiring.stone.data.database.BrokingRow
import br.ufs.hiring.stone.data.database.SavingRow
import br.ufs.hiring.stone.data.database.TransactionRow
import br.ufs.hiring.stone.domain.TransactionType
import br.ufs.hiring.stone.features.wallet.ParseTuples
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import java.util.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */
class ParseTuplesTests {

    private val owner = UUID.randomUUID().toString()

    private val brokingRows = listOf(
            BrokingRow(
                    updatedAt = 123456,
                    label = "bta",
                    buyPrice = 3.14f,
                    sellPrice = 3.13f
            )
    )

    private val savingRows = listOf(
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

    private val transactionsRows = listOf(
            TransactionRow(
                    ownerId = owner,
                    amount = 1.0f,
                    type = "buy",
                    timestamp = "2017-12-13T03:12:31.821",
                    currency = "btc"
            )
    )


    @Test fun `should return parsed information from tuples`() {

        val expected = ParseTuples(brokingRows, savingRows, transactionsRows)

        expected.brokings.forEachIndexed { index, broking ->
            val row = brokingRows[index]
            assertThat(row.label).isEqualTo(broking.currency.label)
            assertThat(row.buyPrice).isEqualTo(broking.buyPrice)
            assertThat(row.sellPrice).isEqualTo(broking.sellPrice)
        }

        expected.savings.forEachIndexed { index, saving ->
            val row = savingRows[index]
            assertThat(row.label).isEqualTo(saving.currency.label)
            assertThat(row.amount).isEqualTo(saving.amount)
        }

        expected.transactions.forEachIndexed { index, transaction ->
            val row = transactionsRows[index]
            assertThat(row.ownerId).isEqualTo(owner)
            assertThat(row.currency).isEqualTo(transaction.currency.label)
            assertThat(row.amount).isEqualTo(transaction.amount)
            assertThat(row.type).isEqualTo(TransactionType.From("buy").toString())
            assertThat(transaction.timestamp).isNotNull()
        }
    }

    @Test fun `should report issue at tuples parsing as infrastructure error`() {

        val corrupted = listOf(
                TransactionRow(
                        ownerId = "Some owner",
                        amount = 1.0f,
                        type = "buy",
                        timestamp = "2017-12-13T03:12:31.821",
                        currency = "USD" // unsupported
                )
        )

        assertThatThrownBy { ParseTuples(brokingRows, savingRows, corrupted) }
                .isEqualTo(LocalDatabaseAccessError)
    }
}