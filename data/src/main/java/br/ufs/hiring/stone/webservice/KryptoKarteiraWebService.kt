package br.ufs.hiring.stone.webservice

import br.ufs.hiring.stone.webservice.models.HomePayload
import br.ufs.hiring.stone.webservice.models.NewTransactionBody
import br.ufs.hiring.stone.webservice.models.NewWalletPayload
import br.ufs.hiring.stone.webservice.models.TransactionResultPayload
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 *
 * Created by @ubiratanfsoares
 *
 */

interface KryptoKarteiraWebService {

    @GET("/v1/wallet/new")
    fun newWallet(): Single<NewWalletPayload>

    @GET("/v1/home/{walletId}/info")
    fun consolidatedHome(
            @Path("walletId") owner: String): Single<HomePayload>

    @POST("/v1/home/{walletId}/transaction")
    fun transaction(
            @Path("walletId") owner: String,
            @Body description: NewTransactionBody): Single<TransactionResultPayload>

}