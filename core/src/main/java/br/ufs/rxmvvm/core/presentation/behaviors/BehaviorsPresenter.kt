package br.ufs.rxmvvm.core.presentation.behaviors

import br.ufs.rxmvvm.core.presentation.errorstate.AssignErrorState
import br.ufs.rxmvvm.core.presentation.loading.LoadingCoreographer
import br.ufs.rxmvvm.core.presentation.networking.NetworkingErrorFeedback
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class BehaviorsPresenter(
        private val view: Any,
        private val scheduler: Scheduler) : ObservableTransformer<Any, Any> {

    override fun apply(upstream: Observable<Any>): ObservableSource<Any> {

        return upstream
                .compose(AssignErrorState(view, scheduler))
                .compose(NetworkingErrorFeedback(view, scheduler))
                .compose(LoadingCoreographer(view, scheduler))
    }

}