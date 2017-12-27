package br.ufs.hiring.stone

import android.view.View
import android.widget.TextView
import br.ufs.architecture.core.errors.InfrastructureError
import br.ufs.architecture.core.errors.NetworkingIssue.InternetUnreachable
import br.ufs.hiring.stone.wallet.WalletActivity
import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.view_error_feedback.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/**
 *
 * Created by @ubiratanfsoares
 *
 */

@RunWith(RobolectricTestRunner::class)
class WalletActivityTests {

    lateinit var activity: WalletActivity

    @Before fun `before each test`() {
        activity = Robolectric.buildActivity(WalletActivity::class.java).create().get()
    }

    @Test fun `should setup views properly at screen creation`() {
        `loading should not be visible`()
        `error related views should not be visible`()
        `recyclerview has nested scrolling disabled for performance reasons`()
        `recyclerview has layoutmanager with prefetching`()
    }

    @Test fun `should delievery actions for progress bar control`() {
        activity.showLoading().run()
        `loading should be visible`()

        activity.hideLoading().run()
        `loading should not be visible`()
    }

    @Test fun `should delievery actions for infrastructure error feedback`() {
        activity.showErrorState(InfrastructureError.RemoteSystemDown).run()
        `error feedback container should be visible`()
        `call to action should be visible with message`(R.string.snacktext_undesired_response)

        activity.hideErrorState().run()
        `error views should be reseted`()
    }

    @Test fun `should delievery actions for internet error report`() {
        activity.reportNetworkingError(InternetUnreachable).run()
        `error feedback container should be visible`()
        `call to action should be visible with message`(R.string.snacktext_internet_connection)

        activity.hideErrorState().run()
        `error views should be reseted`()
    }

    private fun `call to action should be visible with message`(messageResource: Int) {
        val snackText = activity.findViewById<TextView>(R.id.snackbar_text)
        assertThat(snackText).isNotNull()

        val feedback = activity.getString(messageResource)
        assertThat(snackText.text).isEqualTo(feedback)
    }

    private fun `error views should be reseted`() {
        assertThat(activity.feedbackContainer.visibility).isEqualTo(View.GONE)
        assertThat(activity.errorMessage.text).isEqualTo("")
        assertThat(activity.errorImage.drawable).isNull()
    }

    private fun `error feedback container should be visible`() {
        val visibility = activity.feedbackContainer.visibility
        assertThat(visibility).isEqualTo(View.VISIBLE)
    }

    private fun `error related views should not be visible`() {
        val visibility = activity.feedbackContainer.visibility
        assertThat(visibility).isEqualTo(View.GONE)
    }

    private fun `loading should not be visible`() {
        assertThat(activity.pullToRefresh.isEnabled).isTrue()
        assertThat(activity.pullToRefresh.isRefreshing).isFalse()
    }

    private fun `loading should be visible`() {
        assertThat(activity.pullToRefresh.isRefreshing).isTrue()
    }

    private fun `recyclerview has nested scrolling disabled for performance reasons`() {
        assertThat(activity.walletViews.isNestedScrollingEnabled).isFalse()
    }

    private fun `recyclerview has layoutmanager with prefetching`() {
        val layoutManager = activity.walletViews.layoutManager
        assertThat(layoutManager.isItemPrefetchEnabled).isTrue()
        assertThat(layoutManager.isAutoMeasureEnabled).isTrue()
    }

}