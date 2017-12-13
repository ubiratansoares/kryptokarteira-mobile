package br.ufs.architecture.core.presentation.networking

import br.ufs.architecture.core.errors.NetworkingIssue
import io.reactivex.functions.Action

/**
 *
 * Created by @ubiratanfsoares
 *
 */

interface NetworkingErrorView {

    fun reportNetworkingError(issue: NetworkingIssue): Action

}