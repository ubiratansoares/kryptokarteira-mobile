package br.ufs.hiring.stone.data.database

import android.arch.persistence.room.*
import io.reactivex.Maybe

/**
 *
 * Created by @ubiratanfsoares
 *
 */

@Dao
interface SnapshotDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun register(
            snapshot: Snapshot,
            brokings: List<Broking>,
            savings: List<Saving>,
            transactions: List<Transaction>)

    @Update fun update(
            brokings: List<Broking>,
            savings: List<Saving>,
            transactions: List<Transaction>)

    @Query(value = LATEST_SNAPSHOT) fun lastestSnapshot(): Maybe<Snapshot>
    @Query(value = LATEST_BROKINGS) fun brokings(): List<Broking>
    @Query(value = SAVINGS_BY_ID) fun savings(id: String): List<Saving>
    @Query(value = TRANSACTIONS_BY_ID) fun transactions(id: String): List<Transaction>

    private companion object {
        const val LATEST_SNAPSHOT = "SELECT * FROM snapshots LIMIT 1"
        const val LATEST_BROKINGS = "SELECT * FROM brokings"
        const val SAVINGS_BY_ID = "SELECT * FROM savings WHERE savings.ownerId = :id"
        const val TRANSACTIONS_BY_ID = "SELECT * FROM transactions WHERE transactions.ownerId = :id"
    }
}