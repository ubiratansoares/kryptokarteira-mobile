package br.ufs.hiring.stone.features.tests


import br.ufs.hiring.stone.features.onboarding.GiveawayStatus
import br.ufs.hiring.stone.features.onboarding.GiveawayStatus.Received
import br.ufs.hiring.stone.features.onboarding.OnboardingScreen
import br.ufs.hiring.stone.features.onboarding.ReclaimGiveaway
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 *
 * Created by @ubiratanfsoares
 *
 */

@RunWith(RobolectricTestRunner::class)
class OnboardingScreenTests {

    lateinit var usecase: ReclaimGiveaway
    lateinit var screen: OnboardingScreen

    @Before fun `before each test`() {
        usecase = mock()
        screen = OnboardingScreen(usecase)

        val statusChecking: Observable<GiveawayStatus> = Observable.just(Received)

        val execution = Completable.fromAction {
            // Does not matter
        }

        whenever(usecase.now()).thenReturn(execution)
        whenever(usecase.checkStatus()).thenReturn(statusChecking)
    }

    @Test fun `perform onboarding with success`() {

        screen.performOnboard()
                .test()
                .assertComplete()

        `verify onboarding requested with`(attempts = 1)
    }

    @Test fun `check for status with success`() {

        screen.haveReceivedGiveway()
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertValue { it == Received }
    }

    @Test fun `replay previously executed onboarding request`() {
        screen.performOnboard().subscribe()
        screen.performOnboard().test().assertComplete()

        `verify onboarding requested with`(attempts = 1)
    }

    @Test fun `invalidate previously executed onboarding request`() {
        screen.performOnboard().subscribe()

        screen.performOnboard(invalidate = true)
                .test().assertComplete()

        `verify onboarding requested with`(attempts = 2)
    }

    private fun `verify onboarding requested with`(attempts: Int) {
        verify(usecase, times(attempts)).now()
        verifyNoMoreInteractions(usecase)
    }

}