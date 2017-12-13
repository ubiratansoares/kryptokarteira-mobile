package br.ufs.rxmvvm.core.infrastructure.errorhandlers

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class InfraErrorsHandler : ObservableTransformer<Any, Any> {

    override fun apply(upstream: Observable<Any>): ObservableSource<Any> {
        return upstream
                .compose(NetworkingErrorsHandler())
                .compose(RestErrorsHandler())
                .compose(GsonErrorsHandler())
    }

}