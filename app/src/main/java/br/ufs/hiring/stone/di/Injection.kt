package br.ufs.hiring.stone.di

import android.arch.persistence.room.Room
import android.content.Context
import br.ufs.architecture.core.presentation.behaviors.BehaviorsPresenter
import br.ufs.hiring.stone.BuildConfig
import br.ufs.hiring.stone.data.database.SnapshotDAO
import br.ufs.hiring.stone.data.database.SnapshotsDatabase
import br.ufs.hiring.stone.data.storage.HawkOwnerStorage
import br.ufs.hiring.stone.data.storage.WalletOwnerStorage
import br.ufs.hiring.stone.data.webservice.KryptoKarteiraWebService
import br.ufs.hiring.stone.data.webservice.WebServiceFactory
import br.ufs.hiring.stone.domain.*
import br.ufs.hiring.stone.features.onboarding.OnboardingInfrastructure
import br.ufs.hiring.stone.features.onboarding.OnboardingScreen
import br.ufs.hiring.stone.features.transaction.TransactionInfrastructure
import br.ufs.hiring.stone.features.transaction.TransactionScreen
import br.ufs.hiring.stone.features.wallet.RoomPersistance
import br.ufs.hiring.stone.features.wallet.WalletInfrastructure
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

        bind<SnapshotsDatabase>(DB) with singleton {
            Room.databaseBuilder(context, SnapshotsDatabase::class.java, "snapshots-db")
                    .fallbackToDestructiveMigration()
                    .build()
        }

        bind<SnapshotDAO>() with provider {
            val db: SnapshotsDatabase = instance(DB)
            db.dao()
        }

        bind<WalletOwnerStorage>() with singleton {
            HawkOwnerStorage(context.applicationContext)
        }

        bind<ReclaimGiveaway>() with provider {
            OnboardingInfrastructure(
                    storage = instance(),
                    webService = instance(),
                    worker = instance(WORKER)
            )
        }

        bind<RetrieveHomeInformation>() with provider {
            WalletInfrastructure(
                    storage = instance(),
                    webService = instance(),
                    worker = instance(WORKER)
            )
        }

        bind<OfflineHomeSupport>() with provider {
            RoomPersistance(
                    storage = instance(),
                    dao = instance(),
                    worker = instance(WORKER)
            )
        }

        bind<HomeInformationCoordinator>() with provider {
            HomeInformationCoordinator(
                    persister = instance(),
                    updater = instance()
            )
        }

        bind<NewTrasaction>() with provider {
            TransactionInfrastructure(
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
                    usecase = instance(),
                    uiScheduler = instance(UITHREAD)
            )
        }

        bind<TransactionScreen>() with provider {
            TransactionScreen(
                    usecase = instance(),
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
        val DB = "database"
    }

}