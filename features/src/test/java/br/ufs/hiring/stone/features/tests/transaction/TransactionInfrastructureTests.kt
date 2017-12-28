package br.ufs.hiring.stone.features.tests.transaction

import br.ufs.architecture.core.errors.InfrastructureError
import br.ufs.architecture.core.errors.NetworkingIssue
import br.ufs.hiring.stone.data.FileFromResources
import br.ufs.hiring.stone.data.storage.WalletOwnerStorage
import br.ufs.hiring.stone.data.webservice.KryptoKarteiraWebService
import br.ufs.hiring.stone.data.webservice.models.TransactionResultPayload
import br.ufs.hiring.stone.domain.Currency
import br.ufs.hiring.stone.domain.Transaction
import br.ufs.hiring.stone.domain.TransactionError
import br.ufs.hiring.stone.domain.TransactionType
import br.ufs.hiring.stone.features.tests.MockedWebInfrastructureRule
import br.ufs.hiring.stone.features.transaction.TransactionInfrastructure
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import okhttp3.mockwebserver.MockResponse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import java.net.UnknownHostException
import java.util.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class TransactionInfrastructureTests {

    lateinit var storage: WalletOwnerStorage
    lateinit var infrastructure: TransactionInfrastructure

    @get:Rule private val rule = MockedWebInfrastructureRule()

    private val validTransaction = Transaction(
            type = TransactionType.Buy,
            currency = Currency.Bitcoin,
            amount = 1.0f
    )

    private val invalidTransaction = Transaction(
            type = TransactionType.Buy,
            currency = Currency.Real, // broken
            amount = 1.0f
    )

    @Before fun `before each test`() {
        storage = mock()
        infrastructure = TransactionInfrastructure(storage, rule.webservice)
    }

    @Test fun `should integrate successfully with new valid transactions`() {

        `storage reports wallet id present`()
        `backend reports success on transaction`()

        infrastructure.perform(validTransaction)
                .test()
                .assertNoErrors()
                .assertComplete()
    }

    @Test fun `should integrate successfully with invalid transaction, broken contract`() {

        `storage reports wallet id present`()
        `backend reports failure on transaction because payload is broken`()

        infrastructure.perform(invalidTransaction)
                .test()
                .assertError {
                    it == TransactionError.InvalidTransactionParameters
                }
    }

    @Test fun `should integrate successfully with invalid transaction, not allowed`() {

        `storage reports wallet id present`()
        `backend reports failure on transaction because it is not allowed`()

        infrastructure.perform(validTransaction)
                .test()
                .assertError {
                    it == TransactionError.TransactionNotAllowed
                }
    }

    @Test fun `should integrate handling error 4XY`() {

        `storage reports wallet id present`()
        `request fails with`(code = 404)

        infrastructure.perform(validTransaction)
                .test()
                .assertError { it == InfrastructureError.ClientIssue(404) }
    }

    @Test fun `should integrate handling error 5XY`() {

        `storage reports wallet id present`()
        `request fails with`(code = 503)

        infrastructure.perform(validTransaction)
                .test()
                .assertError { it == InfrastructureError.RemoteSystemDown }
    }

    @Test fun `should integrate handling networking issue`() {

        `storage reports wallet id present`()

        val networkingError = Observable.error<TransactionResultPayload> {
            UnknownHostException()
        }

        val mockedWebService = mock<KryptoKarteiraWebService> {
            on { transaction(anyString(), any()) } doReturn networkingError
        }

        TransactionInfrastructure(storage, mockedWebService)
                .perform(validTransaction)
                .test()
                .assertError { it == NetworkingIssue.InternetUnreachable }
    }

    private fun `backend reports success on transaction`() {
        val json = FileFromResources("new_transaction_201OK.json")

        rule.server.enqueue(
                MockResponse()
                        .setResponseCode(201)
                        .setBody(json)
        )
    }

    private fun `backend reports failure on transaction because payload is broken`() {
        val json = FileFromResources("new_transaction_400FAIL.json")

        rule.server.enqueue(
                MockResponse()
                        .setResponseCode(400)
                        .setBody(json)
        )
    }

    private fun `backend reports failure on transaction because it is not allowed`() {
        val json = FileFromResources("new_transaction_409FAIL.json")

        rule.server.enqueue(
                MockResponse()
                        .setResponseCode(409)
                        .setBody(json)
        )
    }

    private fun `request fails with`(code: Int) {
        rule.server.enqueue(MockResponse().setResponseCode(code))
    }

    private fun `storage reports wallet id present`() {
        whenever(storage.retrieveOwner())
                .thenReturn(UUID.randomUUID().toString())
    }

}