package br.ufs.architecture.core.presentation.behaviors

import br.ufs.architecture.core.presentation.errorstate.AssignErrorState
import br.ufs.architecture.core.presentation.loading.LoadingCoreographer
import br.ufs.architecture.core.presentation.networking.NetworkingErrorFeedback
import io.reactivex.*

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class BehaviorsPresenter(
        private val view: Any,
        private val scheduler: Scheduler) :
        ObservableTransformer<Any, Any>, CompletableTransformer {

    override fun apply(upstream: Completable): CompletableSource {
        return pipeline(upstream.toObservable<Any>()).ignoreElements()
    }

    override fun apply(upstream: Observable<Any>): ObservableSource<Any> {
        return pipeline(upstream)
    }

    private fun pipeline(upstream: Observable<Any>): Observable<Any> {
        return upstream
                .compose(AssignErrorState(view, scheduler))
                .compose(NetworkingErrorFeedback(view, scheduler))
                .compose(LoadingCoreographer(view, scheduler))
    }
}