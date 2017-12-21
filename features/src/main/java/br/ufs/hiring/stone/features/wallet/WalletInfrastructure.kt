package br.ufs.hiring.stone.features.wallet

import br.ufs.architecture.core.infrastructure.errorhandlers.InfraErrorsHandler
import br.ufs.hiring.stone.data.storage.WalletOwnerStorage
import br.ufs.hiring.stone.data.webservice.KryptoKarteiraWebService
import br.ufs.hiring.stone.data.webservice.models.HomePayload
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class WalletInfrastructure(
        private val storage: WalletOwnerStorage,
        private val webService: KryptoKarteiraWebService,
        private val worker: Scheduler = Schedulers.trampoline()) : RetrieveWallet {

    override fun execute(): Observable<HomeInformation> {
        return Observable
                .just(storage.retrieveOwner())
                .subscribeOn(worker)
                .switchMap { webService.home(it) }
                .compose(InfraErrorsHandler())
                .map { InformationFromPayload(it as HomePayload) }
    }

}