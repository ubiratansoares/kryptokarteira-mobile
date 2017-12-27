package br.ufs.hiring.stone.features.tests.wallet

import br.ufs.architecture.core.errors.InfrastructureError.LocalDatabaseAccessError
import br.ufs.hiring.stone.data.database.TransactionRow
import br.ufs.hiring.stone.features.tests.fixtures.Fixtures
import br.ufs.hiring.stone.features.wallet.ParseTuples
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

/**
 *
 * Created by @ubiratanfsoares
 *
 */
class ParseTuplesTests {

    private val owner = Fixtures.owner
    private val brokingRows = Fixtures.brokingRows
    private val savingRows = Fixtures.savingRows
    private val transactionsRows = Fixtures.transactionsRows

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
            val label = transaction.currency.label
            val row = transactionsRows[index]
            assertThat(row.ownerId).isEqualTo(owner)
            assertThat(row.currency).isEqualTo(label)
            assertThat(row.amount).isEqualTo(transaction.amount)
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