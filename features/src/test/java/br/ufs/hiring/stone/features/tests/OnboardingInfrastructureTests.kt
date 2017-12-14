package br.ufs.hiring.stone.features.tests

import br.ufs.architecture.core.errors.InfrastructureError
import br.ufs.architecture.core.errors.NetworkingIssue
import br.ufs.hiring.stone.data.FileFromResources
import br.ufs.hiring.stone.data.storage.WalletStorage
import br.ufs.hiring.stone.data.webservice.KryptoKarteiraWebService
import br.ufs.hiring.stone.data.webservice.WebServiceFactory
import br.ufs.hiring.stone.data.webservice.models.WalletPayload
import br.ufs.hiring.stone.features.onboarding.OnboardingInfrastructure
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Observable
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class OnboardingInfrastructureTests {

    lateinit var storage: WalletStorage
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

    @Test fun `should integrate successfully with notice, exposing returned data`() {

        val json = FileFromResources("new_wallet_200OK.json")

        server.enqueue(
                MockResponse()
                        .setResponseCode(200)
                        .setBody(json)
        )

        infrastructure.now()
                .test()
                .assertNoErrors()
                .assertComplete()
    }

    @Test fun `should integrate handling error 4XY`() {

        server.enqueue(MockResponse().setResponseCode(404))

        infrastructure.now()
                .test()
                .assertError { it == InfrastructureError.UndesiredResponse }
    }

    @Test fun `should integrate handling error 5XY`() {

        server.enqueue(MockResponse().setResponseCode(503))

        infrastructure.now()
                .test()
                .assertError { it == InfrastructureError.RemoteSystemDown }
    }

    @Test fun `should integrate handling networking issue`() {

        val networkingError = Observable.error<WalletPayload> {
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
}