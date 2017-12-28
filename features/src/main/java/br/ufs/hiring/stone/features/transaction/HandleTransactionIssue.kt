package br.ufs.hiring.stone.features.transaction

import br.ufs.architecture.core.errors.InfrastructureError.ClientIssue
import br.ufs.hiring.stone.data.webservice.models.TransactionResultPayload
import br.ufs.hiring.stone.domain.TransactionError.InvalidTransactionParameters
import br.ufs.hiring.stone.domain.TransactionError.TransactionNotAllowed
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

/**
 *
 * Created by @ubiratanfsoares
 *
 */

typealias Result = TransactionResultPayload

class HandleTransactionIssue : ObservableTransformer<Result, Result> {

    override fun apply(upstream: Observable<Result>): Observable<Result> {
        return upstream.onErrorResumeNext(this::handleIfTransactionError)
    }

    private fun handleIfTransactionError(error: Throwable): Observable<Result> =
            if (error is ClientIssue) toTransactionIfApplicable(error)
            else Observable.error(error)

    private fun toTransactionIfApplicable(error: ClientIssue): Observable<Result> {
        val toForward = when (error.code) {
            400 -> InvalidTransactionParameters
            409 -> TransactionNotAllowed
            else -> error
        }

        return Observable.error(toForward)
    }


}