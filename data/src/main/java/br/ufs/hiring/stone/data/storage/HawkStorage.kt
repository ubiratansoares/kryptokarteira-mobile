package br.ufs.hiring.stone.data.storage

import android.content.Context
import br.ufs.architecture.core.errors.InfrastructureError
import com.orhanobut.hawk.Hawk

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class HawkStorage(context: Context) : WalletStorage {

    init {
        Hawk.init(context).build()
    }

    override fun storeOwner(owner: WalletOwner) {
        try {
            Hawk.put(WALLET, owner)
        } catch (error: Throwable) {
            throw InfrastructureError.StorageAccessError
        }
    }

    override fun retrieveOwner(): WalletOwner {
        try {
            return Hawk.get(WALLET)
        } catch (error: Throwable) {
            throw InfrastructureError.StorageAccessError
        }
    }

    private companion object {
        val WALLET = "wallet.owner"
    }

}