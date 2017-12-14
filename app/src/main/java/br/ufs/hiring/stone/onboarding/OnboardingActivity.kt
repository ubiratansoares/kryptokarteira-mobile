package br.ufs.hiring.stone.onboarding

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import br.ufs.architecture.core.errors.InfrastructureError
import br.ufs.architecture.core.errors.NetworkingIssue
import br.ufs.architecture.core.presentation.behaviors.BehaviorsPresenter
import br.ufs.architecture.core.presentation.errorstate.ErrorStateView
import br.ufs.architecture.core.presentation.loading.LoadingView
import br.ufs.architecture.core.presentation.networking.NetworkingErrorView
import br.ufs.architecture.core.presentation.util.screenProvider
import br.ufs.hiring.stone.R
import br.ufs.hiring.stone.features.onboarding.OnboardingScreen
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.with
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import kotlinx.android.synthetic.main.activity_onboarding.*
import kotlin.properties.Delegates

class OnboardingActivity : AppCompatActivity(),
        LoadingView, NetworkingErrorView, ErrorStateView {

    private val kodein by lazy { LazyKodein(appKodein) }
    private val presenter by kodein.with(this).instance<BehaviorsPresenter>()
    private val screen by screenProvider { kodein.value.instance<OnboardingScreen>() }

    private var disposable by Delegates.notNull<Disposable>()

    override fun showLoading() = Action {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() = Action {
        progressBar.visibility = View.GONE
    }

    override fun showErrorState(error: InfrastructureError) = Action {
    }


    override fun reportNetworkingError(issue: NetworkingIssue) = Action {

    }

    override fun hideErrorState() = Action {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
    }

    override fun onDestroy() {
        releaseDisposable()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        disposable = screen.performOnboard()
                .compose(presenter)
                .subscribe(
                        {
                            Log.v("Onboarding", "Success!")
                            message.text = "Success!"
                        },
                        { Log.e("Onboarding", "Fail!") }
                )
    }

    private fun releaseDisposable() {
        disposable.dispose()
    }
}
