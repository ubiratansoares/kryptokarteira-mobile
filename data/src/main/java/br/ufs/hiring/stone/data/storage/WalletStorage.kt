package br.ufs.hiring.stone.data.storage

/**
 *
 * Created by @ubiratanfsoares
 *
 */

typealias WalletOwner = String

interface WalletStorage {

    fun storeOwner(owner: WalletOwner)

    fun retrieveOwner(): WalletOwner

}