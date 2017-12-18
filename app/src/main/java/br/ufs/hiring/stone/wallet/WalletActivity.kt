package br.ufs.hiring.stone.wallet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import br.ufs.architecture.core.presentation.behaviors.BehaviorsPresenter
import br.ufs.architecture.core.presentation.loading.LoadingView
import br.ufs.architecture.core.presentation.util.screenProvider
import br.ufs.hiring.stone.R
import br.ufs.hiring.stone.features.wallet.EntryModel
import br.ufs.hiring.stone.features.wallet.WalletScreen
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.with
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import kotlinx.android.synthetic.main.activity_wallet.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class WalletActivity : AppCompatActivity(), LoadingView {

    private val kodein by lazy { LazyKodein(appKodein) }
    private val presentation by kodein.with(this).instance<BehaviorsPresenter>()
    private val screen by screenProvider { kodein.value.instance<WalletScreen>() }

    private val executions by lazy { CompositeDisposable() }

    override fun showLoading() = Action {
        loadingWallet.visibility = View.VISIBLE
    }

    override fun hideLoading() = Action {
        loadingWallet.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        setupViews()
    }

    override fun onDestroy() {
        executions.dispose()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        val entriesAdapter = WalletEntriesAdapter()
        val execution = screen
                .updatedInformation()
                .compose(presentation)
                .doOnSubscribe { walletViews.adapter = entriesAdapter }
                .subscribe(
                        { entriesAdapter.addModel(it as EntryModel) },
                        { Log.e(WalletActivity.TAG, "Failed -> $it") },
                        { entriesAdapter.notifyDataSetChanged() }
                )

        executions.add(execution)
    }

    private fun setupViews() {
        walletViews.layoutManager = LinearLayoutManager(this)
        pullToRefresh.setColorSchemeResources(R.color.colorAccent)
    }

    private companion object {
        val TAG = WalletActivity::class.simpleName
    }

}