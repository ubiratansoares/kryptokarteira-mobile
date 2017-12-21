package br.ufs.hiring.stone.domain

import java.util.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */
data class Transaction(
        val type: TransactionType,
        val currency: Currency,
        val timestamp: Date,
        val amount: Float
)