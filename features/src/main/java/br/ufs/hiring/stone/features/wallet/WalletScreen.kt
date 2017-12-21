package br.ufs.hiring.stone.features.wallet

import br.ufs.architecture.core.presentation.util.Screen
import br.ufs.hiring.stone.domain.RetrieveWallet
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class WalletScreen(
        private val usecase: RetrieveWallet,
        private val uiScheduler: Scheduler = Schedulers.trampoline()) : Screen() {

    fun updatedInformation() = usecase
            .execute()
            .map { EntriesFromWalletInformation(it) }
            .observeOn(uiScheduler, true)

}