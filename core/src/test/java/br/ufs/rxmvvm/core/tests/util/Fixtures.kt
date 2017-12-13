package br.ufs.rxmvvm.core.tests.util

import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

/**
 *
 * Created by @ubiratanfsoares
 *
 */

object Fixtures {

    fun httpError(statusCode: Int, errorMessage: String): HttpException {
        val jsonMediaType = MediaType.parse("application/json")
        val body = ResponseBody.create(jsonMediaType, errorMessage)
        val response: Response<String> = Response.error(statusCode, body)
        return HttpException(response)
    }

}