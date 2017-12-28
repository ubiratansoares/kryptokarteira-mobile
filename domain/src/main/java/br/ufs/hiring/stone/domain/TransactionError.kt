package br.ufs.hiring.stone.domain

/**
 *
 * Created by @ubiratanfsoares
 *
 */

sealed class TransactionError : Throwable() {
    object TransactionNotAllowed : TransactionError()
    object InvalidTransactionParameters : TransactionError()
}