package br.ufs.hiring.stone.domain.tests

import br.ufs.architecture.core.errors.InfrastructureError.LocalDatabaseAccessError
import br.ufs.architecture.core.errors.NetworkingIssue.InternetUnreachable
import br.ufs.hiring.stone.domain.*
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class HomeInformationCoordinatorTests {

    lateinit var persister: OfflineHomeSupport
    lateinit var updater: RetrieveHomeInformation
    lateinit var coordinator: HomeInformationCoordinator

    @Before fun `before each test`() {
        persister = mock()
        updater = mock()
        coordinator = HomeInformationCoordinator(persister, updater)
    }

    @Test fun `at first access, should cache lastest information for home screen`() {
        `no previous information stored on local database`()
        `home information retrieved from remote`()
        `actual data will be saved into database`()

        coordinator.lastInformationAvailable()
                .test()
                .assertComplete()
                .assertNoErrors()

        `verify cache accessed only once`()
        `verify information stored only once`()
        `attempt to fetch remote only once`()
        `no more interactions`()

    }

    @Test fun `at returning access, shows cached information first and updated information after`() {
        `previous information cached`()
        `home information retrieved from remote`()
        `actual data will be saved into database`()

        coordinator.lastInformationAvailable()
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValueCount(2)

        `verify cache accessed only once`()
        `verify information stored only once`()
        `attempt to fetch remote only once`()
        `no more interactions`()

    }

    @Test fun `at returning access, shows cached information and forward error from remote`() {
        `previous information cached`()
        `gathering home information from remote fails`()

        coordinator.lastInformationAvailable()
                .test()
                .assertError { it == InternetUnreachable }

        `verify cache accessed only once`()
        `attempt to fetch remote only once`()
        `verify no attempts to store information`()
        `no more interactions`()
    }

    @Test fun `at returning access, overrides broken cache with new state information`() {
        `previous information corrupted`()
        `home information retrieved from remote`()

        coordinator.lastInformationAvailable()
                .test()
                .assertComplete()
                .assertNoErrors()

        `verify cache accessed only once`()
        `attempt to fetch remote only once`()
        `verify information stored only once`()
        `no more interactions`()
    }

    private fun `no more interactions`() {
        verifyNoMoreInteractions(persister)
        verifyNoMoreInteractions(updater)
    }

    private fun `attempt to fetch remote only once`() {
        verify(updater, times(1)).execute()
    }

    private fun `verify information stored only once`() {
        verify(persister, times(1)).save(any())
    }

    private fun `verify no attempts to store information`() {
        verify(persister, never()).save(any())
    }

    private fun `verify cache accessed only once`() {
        verify(persister, times(1)).previousInformation()
    }

    private fun `actual data will be saved into database`() {
        whenever(persister.save(any())).thenAnswer { Unit }
    }

    private fun `home information retrieved from remote`() {
        whenever(updater.execute()).thenReturn(homeInformationFromRemote())
    }

    private fun `gathering home information from remote fails`() {
        whenever(updater.execute()).thenReturn(noInternet())
    }

    private fun `no previous information stored on local database`() {
        whenever(persister.previousInformation()).thenReturn(Observable.empty())
    }

    private fun `previous information cached`() {
        whenever(persister.previousInformation()).thenReturn(homeInformationFromLocal())
    }

    private fun `previous information corrupted`() {
        whenever(persister.previousInformation()).thenReturn(brokenCache())
    }

    private fun homeInformationFromRemote() = Observable.just(
            HomeInformation(
                    brokings = emptyList(),
                    savings = emptyList(),
                    transactions = emptyList()
            )
    )

    private fun homeInformationFromLocal() = Observable.just(
            HomeInformation(
                    brokings = listOf(
                            BrokingInformation(Currency.Brita, 3.3f, 3.3f)
                    ),
                    savings = emptyList(),
                    transactions = emptyList()
            )
    )

    private fun noInternet() = Observable.error<HomeInformation>(InternetUnreachable)

    private fun brokenCache() = Observable.error<HomeInformation>(LocalDatabaseAccessError)

}