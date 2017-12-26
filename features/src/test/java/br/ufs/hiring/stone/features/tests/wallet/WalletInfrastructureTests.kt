package br.ufs.hiring.stone.features.tests.wallet

import br.ufs.architecture.core.errors.InfrastructureError
import br.ufs.architecture.core.errors.NetworkingIssue
import br.ufs.hiring.stone.data.FileFromResources
import br.ufs.hiring.stone.data.storage.WalletOwnerStorage
import br.ufs.hiring.stone.data.webservice.KryptoKarteiraWebService
import br.ufs.hiring.stone.data.webservice.WebServiceFactory
import br.ufs.hiring.stone.data.webservice.models.HomePayload
import br.ufs.hiring.stone.features.wallet.WalletInfrastructure
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
import java.net.UnknownHostException
import java.util.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */
class WalletInfrastructureTests {

    lateinit var storage: WalletOwnerStorage
    lateinit var infrastructure: WalletInfrastructure
    lateinit var server: MockWebServer

    @Before fun `before each test`() {
        server = MockWebServer()
        storage = mock()
        val serverURL = server.url("/").toString()
        val webservice = WebServiceFactory.create(serverURL)
        infrastructure = WalletInfrastructure(storage, webservice)
    }

    @After fun `after each test`() {
        server.shutdown()
    }

    @Test fun `should integrate successfully with giveaway, exposing returned data`() {

        `storage reports wallet id present`()
        `wallet retrieved with success`()

        infrastructure.execute()
                .test()
                .assertNoErrors()
                .assertComplete()
    }

    @Test fun `should integrate handling error 4XY`() {

        `storage reports wallet id present`()
        `request fails with`(code = 404)

        infrastructure.execute()
                .test()
                .assertError { it == InfrastructureError.UndesiredResponse }
    }

    @Test fun `should integrate handling error 5XY`() {

        `storage reports wallet id present`()
        `request fails with`(code = 503)

        infrastructure.execute()
                .test()
                .assertError { it == InfrastructureError.RemoteSystemDown }
    }

    @Test fun `should integrate handling networking issue`() {

        `storage reports wallet id present`()

        val networkingError = Observable.error<HomePayload> {
            UnknownHostException()
        }

        val mockedWebService = mock<KryptoKarteiraWebService> {
            on { home(anyString()) } doReturn networkingError
        }

        WalletInfrastructure(storage, mockedWebService)
                .execute()
                .test()
                .assertError { it == NetworkingIssue.InternetUnreachable }
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

    private fun `wallet retrieved with success`() {
        val json = FileFromResources("wallet_retrieved_200OK.json")

        server.enqueue(
                MockResponse()
                        .setResponseCode(200)
                        .setBody(json)
        )
    }

}