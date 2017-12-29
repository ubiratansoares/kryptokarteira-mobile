package br.ufs.hiring.stone.data.webservice

import br.ufs.hiring.stone.data.webservice.models.HomePayload
import br.ufs.hiring.stone.data.webservice.models.NewTransactionBody
import br.ufs.hiring.stone.data.webservice.models.NewWalletPayload
import br.ufs.hiring.stone.data.webservice.models.TransactionResultPayload
import io.reactivex.Observable
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

    @GET("v1/wallet/new")
    fun newWallet(): Observable<NewWalletPayload>

    @GET("v1/home/{walletId}/info")
    fun home(@Path("walletId") owner: String): Observable<HomePayload>

    @POST("v1/wallet/{walletId}/transaction")
    fun transaction(
            @Path("walletId") owner: String,
            @Body description: NewTransactionBody): Observable<TransactionResultPayload>

}