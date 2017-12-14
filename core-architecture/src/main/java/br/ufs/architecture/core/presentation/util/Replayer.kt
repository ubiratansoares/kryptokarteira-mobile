package br.ufs.architecture.core.presentation.util

import io.reactivex.*
import io.reactivex.internal.operators.observable.ObservableNever

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class Replayer :
        ObservableTransformer<Any, Any>,
        CompletableTransformer {

    override fun apply(upstream: Completable): CompletableSource {
        return upstream.cache()
    }

    override fun apply(upstream: Observable<Any>): ObservableSource<Any> {
        return upstream
                .replay(BUFFER_COUNT)
                .autoConnect(MAX_SUBSCRIBERS)
    }

    companion object {
        val OBSERVABLE_NEVER: Observable<*> = ObservableNever.INSTANCE
        val COMPLETABLE_NEVER: Completable = Completable.never()
        val MAX_SUBSCRIBERS = 1
        val BUFFER_COUNT = 1
    }
}
