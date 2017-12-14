package br.ufs.hiring.stone.onboarding

import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar.LENGTH_INDEFINITE
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import br.ufs.architecture.core.errors.InfrastructureError
import br.ufs.architecture.core.errors.NetworkingIssue
import br.ufs.architecture.core.presentation.behaviors.BehaviorsPresenter
import br.ufs.architecture.core.presentation.errorstate.ErrorStateView
import br.ufs.architecture.core.presentation.loading.LoadingView
import br.ufs.architecture.core.presentation.networking.NetworkingErrorView
import br.ufs.architecture.core.presentation.util.screenProvider
import br.ufs.hiring.stone.R
import br.ufs.hiring.stone.features.onboarding.OnboardingScreen
import br.ufs.hiring.stone.widgets.action
import br.ufs.hiring.stone.widgets.colorForActionText
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.with
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import kotlinx.android.synthetic.main.activity_onboarding.*
import kotlinx.android.synthetic.main.view_error_feedback.*
import kotlinx.android.synthetic.main.view_giveaway_screen.*
import kotlin.properties.Delegates

class OnboardingActivity : AppCompatActivity(),
        LoadingView, NetworkingErrorView, ErrorStateView {

    private val kodein by lazy { LazyKodein(appKodein) }
    private val presenter by kodein.with(this).instance<BehaviorsPresenter>()
    private val screen by screenProvider { kodein.value.instance<OnboardingScreen>() }

    private var disposable by Delegates.notNull<Disposable>()

    override fun showLoading() = Action {
        loadingIndicator.visibility = View.VISIBLE
    }

    override fun hideLoading() = Action {
        loadingIndicator.visibility = View.GONE
    }

    override fun showErrorState(error: InfrastructureError) = Action {
        showFeedback(error)
        callToAction(
                R.string.snacktext_undesired_response,
                { finish() },
                R.string.snackaction_done
        )
    }

    override fun reportNetworkingError(issue: NetworkingIssue) = Action {
        showFeedback(issue)
        callToAction(
                R.string.snacktext_internet_connection,
                { forceScreenUpdate() }
        )
    }

    override fun hideErrorState() = Action { clearErrorFeedback() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        setupViews()
    }

    override fun onDestroy() {
        releaseDisposable()
        super.onDestroy()
    }

    private fun setupViews() {
        onboardButton.setOnClickListener {
            requestOnboarding()
        }
    }
    private fun releaseDisposable() {
        disposable.dispose()
    }

    private fun callToAction(
            callToActionText: Int,
            retryAction: (Any) -> Unit,
            retryText: Int = R.string.snackaction_retry) {

        Snackbar.make(onboardingRoot, callToActionText, LENGTH_INDEFINITE)
                .action(retryText, retryAction)
                .colorForActionText(R.color.primary)
                .show()
    }

    private fun forceScreenUpdate() {
        releaseDisposable()
        clearErrorFeedback()
        requestOnboarding(forceInvalidation = true)
    }

    private fun clearErrorFeedback() {
        feedbackContainer.visibility = View.GONE
        feedbackContainer.reset()
    }

    private fun showFeedback(error: Throwable) {
        feedbackContainer.visibility = View.VISIBLE
        feedbackContainer.setState(error)
    }

    private fun requestOnboarding(forceInvalidation: Boolean = false) {
        disposable = screen.performOnboard(forceInvalidation)
                .compose(presenter)
                .doOnSubscribe { hideGiveaway() }
                .subscribe(
                        {
                            proceedToHome()
                        },
                        {
                            Log.e("Onboarding", "Fail!")
                        }
                )
    }

    private fun proceedToHome() {
        toast("Cadastrado com sucesso!")
    }

    private fun hideGiveaway() {
        giveawayContainer.visibility = View.INVISIBLE
    }

    private fun showGiveaway() {
        giveawayContainer.visibility = View.VISIBLE
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
