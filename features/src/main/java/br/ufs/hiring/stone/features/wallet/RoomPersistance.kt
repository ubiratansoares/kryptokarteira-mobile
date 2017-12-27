package br.ufs.hiring.stone.features.wallet

import br.ufs.architecture.core.errors.InfrastructureError
import br.ufs.hiring.stone.data.database.*
import br.ufs.hiring.stone.data.storage.WalletOwnerStorage
import br.ufs.hiring.stone.domain.HomeInformation
import br.ufs.hiring.stone.domain.OfflineHomeSupport
import br.ufs.hiring.stone.features.util.ISO8601
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class RoomPersistance(
        private val storage: WalletOwnerStorage,
        private val dao: SnapshotDAO,
        private val worker: Scheduler = Schedulers.trampoline()) : OfflineHomeSupport {

    override fun previousInformation(): Observable<HomeInformation> {
        return dao
                .lastestSnapshot()
                .subscribeOn(worker)
                .map {
                    val brokingTuples = dao.brokings()
                    val savingTuples = dao.savings(it.ownerId)
                    val transactionTuples = dao.transactions(it.ownerId)
                    ParseTuples(brokingTuples, savingTuples, transactionTuples)
                }
                .toObservable()
    }

    override fun save(info: HomeInformation) {
        val now = System.currentTimeMillis()
        val owner = storage.retrieveOwner()

        val snapshot = Snapshot(owner)

        val brokings = info.brokings.map {
            BrokingRow(now, it.currency.label, it.buyPrice, it.sellPrice)
        }

        val savings = info.savings.map {
            SavingRow(owner, it.currency.label, it.amount)
        }

        val transactions = info.transactions.map {
            TransactionRow(
                    ownerId = owner,
                    currency = it.currency.label,
                    type = it.type.toString(),
                    amount = it.amount,
                    timestamp = ISO8601.unzonedStringFromDate(it.timestamp)
            )
        }

        try {
            if (!dao.lastestSnapshot().isEmpty.blockingGet()) {
                dao.update(brokings, savings, transactions)
            } else {
                dao.register(snapshot, brokings, savings, transactions)
            }

        } catch (exp: Throwable) {
            throw InfrastructureError.LocalDatabaseAccessError
        }
    }


    private companion object {
        val TAG = "RoomPersistance"
    }
}