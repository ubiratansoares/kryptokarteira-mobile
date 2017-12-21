package br.ufs.hiring.stone.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 *
 * Created by @ubiratanfsoares
 *
 */

@Database(
        version = 1,
        entities = [Snapshot::class, Broking::class, Saving::class, Transaction::class]
)
abstract class SnapshotsDatabase : RoomDatabase() {

    abstract fun dao(): SnapshotDAO

}