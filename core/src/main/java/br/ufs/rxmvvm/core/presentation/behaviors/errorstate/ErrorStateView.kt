package br.ufs.rxmvvm.core.presentation.errorstate

import br.ufs.rxmvvm.core.errors.InfrastructureError
import io.reactivex.functions.Action

/**
 *
 * Created by @ubiratanfsoares
 *
 */

interface ErrorStateView {

    fun showErrorState(error: InfrastructureError): Action

    fun hideErrorState(): Action

}