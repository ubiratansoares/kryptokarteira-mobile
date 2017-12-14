package br.ufs.hiring.stone.data.webservice

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *
 * Created by @ubiratanfsoares
 *
 */

object WebServiceFactory {

    private val BASE_URL = "https://kryptokarteira.herokuapp.com/api"
    private val DEFAULT_TIMEOUT_SECONDS = 15L

    fun create(debuggable: Boolean = false): KryptoKarteiraWebService {

        val logger = createLogger(debuggable)
        val httpClient = createHttpClient(logger = logger)
        val converter = GsonConverterFactory.create()
        val rxAdapter = RxJava2CallAdapterFactory.create()

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(converter)
                .addCallAdapterFactory(rxAdapter)
                .build()

        return retrofit.create(KryptoKarteiraWebService::class.java)
    }


    private fun createLogger(debuggable: Boolean): Interceptor {
        val loggingLevel = if (debuggable) Level.BODY else Level.NONE
        return HttpLoggingInterceptor().apply { level = loggingLevel }
    }

    private fun createHttpClient(logger: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(logger)
                .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
    }


}