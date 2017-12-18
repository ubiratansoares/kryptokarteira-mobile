package br.ufs.hiring.stone.di

import android.content.Context
import br.ufs.architecture.core.presentation.behaviors.BehaviorsPresenter
import br.ufs.hiring.stone.BuildConfig
import br.ufs.hiring.stone.data.storage.HawkStorage
import br.ufs.hiring.stone.data.storage.WalletStorage
import br.ufs.hiring.stone.data.webservice.KryptoKarteiraWebService
import br.ufs.hiring.stone.data.webservice.WebServiceFactory
import br.ufs.hiring.stone.features.onboarding.OnboardingInfrastructure
import br.ufs.hiring.stone.features.onboarding.OnboardingScreen
import br.ufs.hiring.stone.features.onboarding.ReclaimGiveaway
import br.ufs.hiring.stone.features.wallet.WalletScreen
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.androidActivityScope
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class Injection(private val context: Context) {


    val graph = Kodein.lazy {

        bind<Scheduler>(WORKER) with singleton { Schedulers.io() }
        bind<Scheduler>(UITHREAD) with singleton { AndroidSchedulers.mainThread() }

        bind<KryptoKarteiraWebService>() with singleton {
            WebServiceFactory.create(debuggable = BuildConfig.DEBUG)
        }

        bind<WalletStorage>() with singleton {
            HawkStorage(context.applicationContext)
        }

        bind<ReclaimGiveaway>() with provider {
            OnboardingInfrastructure(
                    storage = instance(),
                    webService = instance(),
                    worker = instance(WORKER)
            )
        }

        bind<OnboardingScreen>() with provider {
            OnboardingScreen(
                    usecase = instance(),
                    uiScheduler = instance(UITHREAD)
            )
        }

        bind<WalletScreen>() with provider {
            WalletScreen(
                    uiScheduler = instance(UITHREAD)
            )
        }

        bind<BehaviorsPresenter>() with scopedSingleton(androidActivityScope) {
            BehaviorsPresenter(
                    view = it,
                    scheduler = instance(UITHREAD)
            )
        }
    }

    companion object {
        val WORKER = "worker"
        val UITHREAD = "main"
    }

}