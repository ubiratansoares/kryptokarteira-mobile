package br.ufs.hiring.stone.features.tests.wallet

import br.ufs.architecture.core.errors.InfrastructureError
import br.ufs.hiring.stone.data.database.Snapshot
import br.ufs.hiring.stone.data.database.SnapshotDAO
import br.ufs.hiring.stone.data.storage.WalletOwnerStorage
import br.ufs.hiring.stone.features.tests.fixtures.Fixtures
import br.ufs.hiring.stone.features.wallet.ParseTuples
import br.ufs.hiring.stone.features.wallet.RoomPersistance
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Maybe
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Test

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class RoomPersistanceTests {

    lateinit var storage: WalletOwnerStorage
    lateinit var dao: SnapshotDAO
    lateinit var persistance: RoomPersistance

    val storedBroking = Fixtures.brokingRows
    val storedSavings = Fixtures.savingRows
    val storedTransactions = Fixtures.transactionsRows

    @Before fun `before each test`() {
        storage = mock()
        dao = mock()
        persistance = RoomPersistance(storage, dao)
    }

    @Test fun `should retrieve cached information when available`() {
        `dao reports content available on database`()

        persistance.previousInformation()
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue {
                    it == ParseTuples(
                            brokingRows = storedBroking,
                            savingRows = storedSavings,
                            transactionRows = storedTransactions)
                }

        `verify previous information queried`()
        `verify no more interaction with database`()
    }

    @Test fun `should report no previous content available`() {
        `dao reports no previous content available`()

        persistance.previousInformation()
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertNoValues()

        `verify only snapshot queried`()
        `verify no more interaction with database`()
    }

    @Test fun `should save information for new home state, no previous content`() {
        `storage returns saved wallet owner`()
        `dao reports no previous content available`()

        val incoming = Fixtures.homeInformation

        persistance.save(info = incoming)

        `verify register for first time`()
        `verify no updates on database`()
        `verify only snapshot queried`()
        `verify no more interaction with database`()
    }

    @Test fun `should report failure at database persistance`() {
        `storage returns saved wallet owner`()
        `dao reports no previous content available`()
        `dao fails to register on database`()

        val incoming = Fixtures.homeInformation

        assertThatThrownBy { persistance.save(info = incoming) }
                .isEqualTo(InfrastructureError.LocalDatabaseAccessError)

        `verify register for first time`()
        `verify no updates on database`()
        `verify only snapshot queried`()
        `verify no more interaction with database`()
    }

    private fun `dao fails to register on database`() {
        whenever(dao.register(any(), any(), any(), any())).then {
            throw IllegalArgumentException("Ops!")
        }
    }

    @Test fun `should update information for new home state, when previous content available`() {
        `storage returns saved wallet owner`()
        `dao reports content available on database`()

        val incoming = Fixtures.homeInformation

        persistance.save(info = incoming)

        `verify previous content updated`()
        `verify no new record created on database`()
        `verify only snapshot queried`()
        `verify no more interaction with database`()
    }

    private fun `verify no new record created on database`() {
        verify(dao, never()).register(any(), any(), any(), any())
    }

    private fun `verify register for first time`() {
        verify(dao, times(1)).register(any(), any(), any(), any())
    }

    private fun `verify no updates on database`() {
        verify(dao, never()).update(any(), any(), any())
    }

    private fun `verify previous content updated`() {
        verify(dao, times(1)).update(any(), any(), any())
    }

    private fun `verify previous information queried`() {
        verify(dao, times(1)).lastestSnapshot()
        verify(dao, times(1)).brokings()
        verify(dao, times(1)).savings(any())
        verify(dao, times(1)).transactions(any())
    }

    private fun `verify only snapshot queried`() {
        verify(dao, times(1)).lastestSnapshot()
        verify(dao, never()).brokings()
        verify(dao, never()).savings(any())
        verify(dao, never()).transactions(any())
    }

    private fun `verify no more interaction with database`() {
        verifyNoMoreInteractions(dao)
    }

    private fun `dao reports content available on database`() {
        val walletID = Fixtures.owner
        val present = Maybe.just(Snapshot(walletID))

        whenever(dao.lastestSnapshot()).thenReturn(present)
        whenever(dao.brokings()).thenReturn(storedBroking)
        whenever(dao.savings(walletID)).thenReturn(storedSavings)
        whenever(dao.transactions(walletID)).thenReturn(storedTransactions)
    }

    private fun `dao reports no previous content available`() {
        val absent = Maybe.empty<Snapshot>()
        whenever(dao.lastestSnapshot()).thenReturn(absent)
    }

    private fun `storage returns saved wallet owner`() {
        whenever(storage.retrieveOwner()).thenReturn(Fixtures.owner)
    }
}