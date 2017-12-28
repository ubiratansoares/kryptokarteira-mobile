package br.ufs.hiring.stone.features.tests

import br.ufs.hiring.stone.data.webservice.WebServiceFactory
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.ExternalResource
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger


/**
 *
 * Created by @ubiratanfsoares
 *
 */

class MockedWebInfrastructureRule : ExternalResource() {

    private val logger = Logger.getLogger(MockedWebInfrastructureRule::class.java.name)
    private var started: Boolean = false

    val server by lazy { MockWebServer() }

    val webservice by lazy {
        val serverURL = server.url("/").toString()
        WebServiceFactory.create(serverURL)
    }

    override fun before() {
        if (started) return
        started = true
        try {
            server.start()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    override fun after() {
        try {
            server.shutdown()
        } catch (e: IOException) {
            logger.log(Level.WARNING, "MockWebServer shutdown failed", e)
        }
    }


}