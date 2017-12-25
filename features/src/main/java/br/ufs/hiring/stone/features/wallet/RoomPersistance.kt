package br.ufs.hiring.stone.features.wallet

import android.util.Log
import br.ufs.architecture.core.errors.InfrastructureError
import br.ufs.hiring.stone.data.database.*
import br.ufs.hiring.stone.data.storage.WalletOwnerStorage
import br.ufs.hiring.stone.domain.HomeInformation
import br.ufs.hiring.stone.domain.OfflineHomeSupport
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class RoomPersistance(
        private val storage: WalletOwnerStorage,
        private val database: SnapshotsDatabase,
        private val worker: Scheduler = Schedulers.trampoline()) : OfflineHomeSupport {

    override fun previousInformation(): Observable<HomeInformation> {
        val dao = database.dao()
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
        val timestamp = System.currentTimeMillis()
        val owner = storage.retrieveOwner()

        val snapshot = Snapshot(owner)

        val brokings = info.brokings.map {
            BrokingRow(timestamp, it.currency.label, it.buyPrice, it.sellPrice)
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
                    timestamp = toISO8601(it.timestamp))
        }

        try {
            val dao = database.dao()
            if (!dao.lastestSnapshot().isEmpty.blockingGet()) {
                dao.update(brokings, savings, transactions)
            } else {
                dao.register(snapshot, brokings, savings, transactions)
            }

        } catch (exp: Throwable) {
            Log.e(TAG, exp.message)
            throw InfrastructureError.LocalDatabaseAccessError
        }
    }

    private fun toISO8601(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
        return formatter.format(date)
    }

    private companion object {
        val TAG = "RoomPersistance"
    }
}