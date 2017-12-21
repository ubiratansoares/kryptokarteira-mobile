package br.ufs.hiring.stone.data.storage

import android.content.Context
import br.ufs.architecture.core.errors.InfrastructureError
import br.ufs.hiring.stone.data.storage.WalletOwnerStorage.Companion.NO_WALLET
import com.orhanobut.hawk.Hawk

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class HawkOwnerStorage(context: Context) : WalletOwnerStorage {

    init {
        Hawk.init(context).build()
    }

    override fun storeOwner(owner: WalletOwner) {
        try {
            Hawk.put(WALLET, "5a3009883fa720170001ce55")
        } catch (error: Throwable) {
            throw InfrastructureError.StorageAccessError
        }
    }

    override fun retrieveOwner(): WalletOwner {
        try {
            return "5a3009883fa720170001ce55"
            return Hawk.get(WALLET, NO_WALLET)
        } catch (error: Throwable) {
            throw InfrastructureError.StorageAccessError
        }
    }

    private companion object {
        val WALLET = "wallet.owner"
    }

}