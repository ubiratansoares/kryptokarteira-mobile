package br.ufs.hiring.stone.features.tests

import br.ufs.architecture.core.errors.InfrastructureError
import br.ufs.architecture.core.errors.InfrastructureError.RemoteSystemDown
import br.ufs.architecture.core.errors.InfrastructureError.UndesiredResponse
import br.ufs.architecture.core.errors.NetworkingIssue
import br.ufs.hiring.stone.data.FileFromResources
import br.ufs.hiring.stone.data.storage.WalletOwnerStorage
import br.ufs.hiring.stone.data.webservice.KryptoKarteiraWebService
import br.ufs.hiring.stone.data.webservice.WebServiceFactory
import br.ufs.hiring.stone.data.webservice.models.NewWalletPayload
import br.ufs.hiring.stone.features.onboarding.GiveawayStatus
import br.ufs.hiring.stone.features.onboarding.OnboardingInfrastructure
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import java.io.IOException
import java.util.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class OnboardingInfrastructureTests {

    lateinit var storage: WalletOwnerStorage
    lateinit var infrastructure: OnboardingInfrastructure
    lateinit var server: MockWebServer

    @Before fun `before each test`() {
        server = MockWebServer()
        storage = mock()
        val serverURL = server.url("/").toString()
        val webservice = WebServiceFactory.create(serverURL)
        infrastructure = OnboardingInfrastructure(storage, webservice)
    }

    @After fun `after each test`() {
        server.shutdown()
    }

    @Test fun `should integrate successfully with giveaway, exposing returned data`() {

        `server returns a new wallet`()

        infrastructure.now()
                .test()
                .assertNoErrors()
                .assertComplete()
    }

    @Test fun `should report giveway already received`() {
        `storage reports wallet id present`()
        `verify giveway status`(GiveawayStatus.Received)
    }

    @Test fun `should report giveway available to be received`() {
        `storage reports wallet not present`()
        `verify giveway status`(GiveawayStatus.Available)
    }

    @Test fun `should integrate handling error 4XY`() {

        `request fails with`(code = 404)

        infrastructure.now()
                .test()
                .assertError { it == UndesiredResponse }
    }

    @Test fun `should integrate handling error 5XY`() {

        `request fails with`(code = 503)

        infrastructure.now()
                .test()
                .assertError { it == RemoteSystemDown }
    }

    @Test fun `should integrate handling networking issue`() {

        val networkingError = Observable.error<NewWalletPayload> {
            IOException("Canceled")
        }

        val mockedWebService = mock<KryptoKarteiraWebService> {
            on { newWallet() } doReturn networkingError
        }

        OnboardingInfrastructure(storage, mockedWebService)
                .now()
                .test()
                .assertError { it == NetworkingIssue.ConnectionSpike }
    }

    @Test fun `should integrate handling storage access issue`() {

        `server returns a new wallet`()

        whenever(storage.storeOwner(anyString()))
                .then {
                    throw InfrastructureError.StorageAccessError
                }

        infrastructure.now()
                .test()
                .assertError { it == InfrastructureError.StorageAccessError }

    }

    private fun `verify giveway status`(status: GiveawayStatus) {
        infrastructure.checkStatus()
                .test()
                .assertNoErrors()
                .assertComplete()
                .assertValue { it == status }
    }

    private fun `request fails with`(code: Int) {
        server.enqueue(
                MockResponse().setResponseCode(code)
        )
    }

    private fun `storage reports wallet id present`() {
        whenever(storage.retrieveOwner())
                .thenReturn(UUID.randomUUID().toString())
    }

    private fun `storage reports wallet not present`() {
        whenever(storage.retrieveOwner())
                .thenReturn(WalletOwnerStorage.NO_WALLET)
    }

    private fun `server returns a new wallet`() {
        val json = FileFromResources("new_wallet_200OK.json")

        server.enqueue(
                MockResponse()
                        .setResponseCode(200)
                        .setBody(json)
        )
    }
}