package br.ufs.hiring.stone.data.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

/**
 *
 * Created by @ubiratanfsoares
 *
 */

@Entity(
        tableName = "snapshots",
        primaryKeys = ["ownerId"],
        indices = [Index(value = ["ownerId"], unique = true)]
)
class Snapshot(val ownerId: String)

@Entity(tableName = "brokings")
class Broking(
        val updatedAt: Long,
        @PrimaryKey val label: String,
        val buyPrice: Float,
        val sellPrice: Float
)

@Entity(
        tableName = "savings",
        primaryKeys = ["ownerId", "label"],
        foreignKeys = [(
                ForeignKey(
                        entity = Snapshot::class,
                        parentColumns = arrayOf("ownerId"),
                        childColumns = arrayOf("ownerId")
                ))
        ]
)
class Saving(
        val ownerId: String,
        val label: String,
        val amount: Float
)

@Entity(
        tableName = "transactions",
        primaryKeys = ["ownerId", "timestamp"],
        foreignKeys = [(
                ForeignKey(
                        entity = Snapshot::class,
                        parentColumns = arrayOf("ownerId"),
                        childColumns = arrayOf("ownerId")
                ))
        ]
)
class Transaction(
        val ownerId: String,
        val currency: String,
        val type: String,
        val amount: Float,
        val timestamp: String
)