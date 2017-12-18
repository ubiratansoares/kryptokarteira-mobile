package br.ufs.hiring.stone.features.wallet

import br.ufs.architecture.core.presentation.util.Screen
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class WalletScreen(
        private val uiScheduler: Scheduler = Schedulers.trampoline()) : Screen() {

    fun updatedInformation(): Observable<EntryModel> =
            Observable.fromIterable(fakes)
                    .delay(3, TimeUnit.SECONDS)
                    .observeOn(uiScheduler)

}