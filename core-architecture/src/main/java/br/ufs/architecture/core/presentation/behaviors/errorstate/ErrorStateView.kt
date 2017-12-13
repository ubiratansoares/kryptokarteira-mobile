package br.ufs.architecture.core.presentation.errorstate

import br.ufs.architecture.core.errors.InfrastructureError
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