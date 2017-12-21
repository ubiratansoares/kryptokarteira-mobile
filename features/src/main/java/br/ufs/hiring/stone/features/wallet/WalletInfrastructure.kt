package br.ufs.hiring.stone.features.wallet

import android.util.Log
import br.ufs.architecture.core.errors.InfrastructureError
import br.ufs.architecture.core.infrastructure.errorhandlers.InfraErrorsHandler
import br.ufs.hiring.stone.data.database.*
import br.ufs.hiring.stone.data.database.Saving
import br.ufs.hiring.stone.data.database.Transaction
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
        private val database: SnapshotsDatabase,
        private val storage: WalletOwnerStorage,
        private val webService: KryptoKarteiraWebService,
        private val worker: Scheduler = Schedulers.trampoline()) : RetrieveWallet {

    override fun execute(): Observable<HomeInformation> {
        val fromNetwork = Observable
                .just(storage.retrieveOwner())
                .subscribeOn(worker)
                .flatMap { webService.home(it) }
                .compose(InfraErrorsHandler())
                .doOnNext { saveSnapshot(it as HomePayload) }
                .map { InformationFromPayload(it as HomePayload) }

        return restorePreviousInformation().concatWith(fromNetwork)
    }

    private fun restorePreviousInformation(): Observable<HomeInformation> {
        val dao = database.dao()
        return dao.lastestSnapshot()
                .subscribeOn(worker)
                .map {
                    val t1 = dao.brokings()
                    val t2 = dao.savings(it.ownerId)
                    val t3 = dao.transactions(it.ownerId)
                    InformationFromDatabase(t1, t2, t3)
                }
                .toObservable()
    }

    private fun saveSnapshot(it: HomePayload) {
        val timestamp = System.currentTimeMillis()
        val ownerId = "5a3009883fa720170001ce55"
        val snapshot = Snapshot(ownerId)

        val brokings = it.broking.map {
            Broking(timestamp, it.label, it.buyPrice, it.sellPrice)
        }

        val savings = it.wallet.savings.map {
            Saving(ownerId, it.label, it.amount)
        }

        val transactions = it.wallet.transactions.map {
            Transaction(ownerId, it.currency, it.type, it.amount, it.timestamp)
        }

        try {
            val dao = database.dao()

            if (!dao.lastestSnapshot().isEmpty.blockingGet()) {
                dao.update(brokings, savings, transactions)
            } else {
                dao.register(snapshot, brokings, savings, transactions)
            }

        } catch (exp: Throwable) {
            Log.e("WalletInfrastructure", exp.message)
            throw InfrastructureError.LocalDatabaseAccessError
        }
    }

}