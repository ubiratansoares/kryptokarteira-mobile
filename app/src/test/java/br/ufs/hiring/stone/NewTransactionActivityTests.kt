package br.ufs.hiring.stone

import android.view.View
import android.widget.TextView
import br.ufs.architecture.core.errors.InfrastructureError
import br.ufs.architecture.core.errors.NetworkingIssue
import br.ufs.hiring.stone.transaction.NewTransactionActivity
import kotlinx.android.synthetic.main.activity_new_transaction.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 *
 * Created by @ubiratanfsoares
 *
 */
@RunWith(RobolectricTestRunner::class)
class NewTransactionActivityTests {

    lateinit var activity: NewTransactionActivity

    @Before fun `before each test`() {
        val intent = NewTransactionActivity.launchIntent(
                from = RuntimeEnvironment.application,
                operationType = "buy",
                currencyLabel = "btc"
        )

        activity = Robolectric
                .buildActivity(NewTransactionActivity::class.java, intent)
                .create()
                .get()
    }

    @Test fun `should setup views properly at screen creation`() {
        `loading should not be visible`()
        `transaction related views should be visible`()
    }

    @Test fun `should delievery actions for progress bar control`() {
        activity.showLoading().run()
        `loading should be visible`()

        activity.hideLoading().run()
        `loading should not be visible`()
    }

    @Test fun `should delievery actions for infrastructure error feedback`() {
        activity.reportNetworkingError(NetworkingIssue.InternetUnreachable).run()
        `call to action should be visible with message`(R.string.snacktext_internet_connection)
    }

    @Test fun `should delievery actions for internet error report`() {
        activity.showErrorState(InfrastructureError.RemoteSystemDown).run()
        `call to action should be visible with message`(R.string.snacktext_undesired_response)
    }

    private fun `loading should be visible`() {
        assertThat(activity.dialog.isShowing).isTrue()
    }

    private fun `loading should not be visible`() {
        assertThat(activity.dialog.isShowing).isFalse()
    }

    private fun `transaction related views should be visible`() {
        assertThat(activity.transactionScreenRoot.visibility).isEqualTo(View.VISIBLE)

    }

    private fun `call to action should be visible with message`(messageResource: Int) {
        val snackText = activity.findViewById<TextView>(R.id.snackbar_text)
        assertThat(snackText).isNotNull()

        val feedback = activity.getString(messageResource)
        assertThat(snackText.text).isEqualTo(feedback)
    }

}