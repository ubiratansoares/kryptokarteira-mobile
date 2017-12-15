package br.ufs.hiring.stone.features.onboarding

import br.ufs.architecture.core.presentation.util.Replayer
import br.ufs.architecture.core.presentation.util.Replayer.Companion.COMPLETABLE_NEVER
import br.ufs.architecture.core.presentation.util.Screen
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class OnboardingScreen(
        private val usecase: ReclaimGiveaway,
        private val uiScheduler: Scheduler = Schedulers.trampoline()) : Screen() {

    private var replayable: Completable = COMPLETABLE_NEVER

    fun haveReceivedGiveway(): Observable<GiveawayStatus> = usecase.checkStatus()

    fun performOnboard(invalidate: Boolean = false): Completable {
        if (invalidate) reset()
        if (replayable == COMPLETABLE_NEVER) replayable = assignReplayer()
        return replayable
    }

    private fun assignReplayer(): Completable {
        return usecase
                .now()
                .observeOn(uiScheduler)
                .compose(Replayer())
    }

    private fun reset() {
        replayable = Completable.never()
    }
}