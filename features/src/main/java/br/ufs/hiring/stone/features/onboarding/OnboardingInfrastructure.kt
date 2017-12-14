package br.ufs.hiring.stone.features.onboarding

import br.ufs.architecture.core.infrastructure.errorhandlers.InfraErrorsHandler
import br.ufs.hiring.stone.data.storage.WalletStorage
import br.ufs.hiring.stone.data.webservice.KryptoKarteiraWebService
import br.ufs.hiring.stone.data.webservice.models.WalletPayload
import io.reactivex.Completable
import io.reactivex.Scheduler

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class OnboardingInfrastructure internal constructor(
        private val worker: Scheduler,
        private val storage: WalletStorage,
        private val webService: KryptoKarteiraWebService) : ReclaimGiveaway {

    override fun now(): Completable {
        return webService.newWallet()
                .subscribeOn(worker)
                .compose(InfraErrorsHandler())
                .map { it as WalletPayload }
                .doOnNext { storage.storeOwner(it.owner) }
                .ignoreElements()
    }

}