package br.ufs.rxmvvm.core.presentation.networking

import br.ufs.rxmvvm.core.errors.NetworkingIssue
import io.reactivex.functions.Action

/**
 *
 * Created by @ubiratanfsoares
 *
 */

interface NetworkingErrorView {

    fun reportNetworkingError(issue: NetworkingIssue): Action

}