package br.ufs.hiring.stone.features.tests.wallet

import br.ufs.hiring.stone.features.tests.fixtures.Fixtures
import br.ufs.hiring.stone.features.wallet.*
import br.ufs.hiring.stone.features.wallet.EntryType.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class EntriesFromWalletInformationTests {

    val fullHome = Fixtures.homeInformation

    @Test fun `should assemble all entries with home information`() {
        val entries = EntriesFromWalletInformation(fullHome)

        val brokings = fullHome.brokings.size
        val savings = fullHome.savings.size
        val transactions = fullHome.transactions.size

        val expectedSections = 3

        `check entries matching`(
                models = entries,
                sectionsCount = expectedSections,
                savingsCount = savings,
                brokingsCount = brokings,
                transactionsCount = transactions
        )
    }

    @Test fun `should not expose transactions entries when no transaction available`() {
        val noTransactions = fullHome.copy(transactions = emptyList())
        val entries = EntriesFromWalletInformation(noTransactions)

        val brokings = noTransactions.brokings.size
        val savings = noTransactions.savings.size
        val transactions = noTransactions.transactions.size

        val expectedSections = 2

        `check entries matching`(
                models = entries,
                sectionsCount = expectedSections,
                savingsCount = savings,
                brokingsCount = brokings,
                transactionsCount = transactions
        )
    }

    @Test fun `should expose single transaction as standalone entry`() {
        val singleTransaction = fullHome.copy(transactions = fullHome.transactions.take(1))
        val entries = EntriesFromWalletInformation(singleTransaction)

        assertThat(entries).matches {
            val entry = it.first { model -> model is TransactionEntry } as TransactionEntry
            entry.type == Standalone
        }
    }

    @Test fun `should expose two transactions as header and footer entries`() {
        val twoTransactions = fullHome.copy(transactions = fullHome.transactions.take(2))
        val entries = EntriesFromWalletInformation(twoTransactions)

        assertThat(entries)
                .matches { firstAsTransactionEntry(it).type == Header }
                .matches { lastAsTransactionEntry(it).type == Footer }
    }

    private fun `check entries matching`(
            models: List<EntryModel>,
            sectionsCount: Int,
            savingsCount: Int,
            brokingsCount: Int,
            transactionsCount: Int) {

        assertThat(models)
                .matches { investiments(it) == savingsCount }
                .matches { tradingValues(it) == brokingsCount * 2 }
                .matches { tradingHeadlines(it) == brokingsCount }
                .matches { transactionsEntries(it) == transactionsCount }
                .matches { cardHeaders(it) == sectionsCount }
                .matches { cardFooters(it) == sectionsCount }
                .matches { cardFooters(it) == sectionsCount }
                .matches { blockHeadlines(it) == sectionsCount }
    }

    private fun firstAsTransactionEntry(entries: List<EntryModel>) =
            entries.first { it is TransactionEntry } as TransactionEntry

    private fun lastAsTransactionEntry(entries: List<EntryModel>) =
            entries.last { it is TransactionEntry } as TransactionEntry

    private fun cardHeaders(entries: List<EntryModel>) = entries.count {
        it is CardHeader
    }

    private fun cardFooters(entries: List<EntryModel>) = entries.count {
        it is CardFooter
    }

    private fun investiments(entries: List<EntryModel>) = entries.count {
        it is Investiment
    }

    private fun tradingValues(entries: List<EntryModel>) = entries.count {
        it is TradeValue
    }

    private fun tradingHeadlines(entries: List<EntryModel>) = entries.count {
        it is Headline && it.type == HeadlineType.Trade
    }

    private fun blockHeadlines(entries: List<EntryModel>) = entries.count {
        it is Headline && it.type == HeadlineType.Block
    }

    private fun transactionsEntries(entries: List<EntryModel>) = entries.count {
        it is TransactionEntry
    }
}