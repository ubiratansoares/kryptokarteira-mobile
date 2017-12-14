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

    private val BASE_URL = "https://kryptokarteira.herokuapp.com/api/"
    private val DEFAULT_TIMEOUT_SECONDS = 15L

    fun create(apiURL: String = BASE_URL,
               debuggable: Boolean = false): KryptoKarteiraWebService {

        val logger = createLogger(debuggable)
        val httpClient = createHttpClient(logger = logger)
        val converter = GsonConverterFactory.create()
        val rxAdapter = RxJava2CallAdapterFactory.create()

        val retrofit = Retrofit.Builder()
                .baseUrl(apiURL)
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

    private fun authorizer(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()

            val authorized = request.newBuilder()
                    .addHeader("Authorization", "8aaacf6a-cf13-4378-a33d-0b1dec00ad16")
                    .build()
            chain.proceed(authorized)
        }
    }

    private fun createHttpClient(logger: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(logger)
                .addInterceptor(authorizer())
                .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()
    }


}