package br.ufs.hiring.stone.features.transaction

import br.ufs.architecture.core.infrastructure.errorhandlers.InfraErrorsHandler
import br.ufs.hiring.stone.data.storage.WalletOwnerStorage
import br.ufs.hiring.stone.data.webservice.KryptoKarteiraWebService
import br.ufs.hiring.stone.data.webservice.models.TransactionResultPayload
import br.ufs.hiring.stone.domain.NewTrasaction
import br.ufs.hiring.stone.domain.Transaction
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class TransactionInfrastructure(
        private val storage: WalletOwnerStorage,
        private val webService: KryptoKarteiraWebService,
        private val worker: Scheduler = Schedulers.trampoline()) : NewTrasaction {

    override fun perform(desired: Transaction): Completable {
        return Observable
                .just(storage.retrieveOwner())
                .unsubscribeOn(worker)
                .flatMap { webService.transaction(it, ToTransactionBody(desired)) }
                .compose(InfraErrorsHandler())
                .map { it as TransactionResultPayload }
                .compose(HandleTransactionIssue())
                .ignoreElements()
    }
}