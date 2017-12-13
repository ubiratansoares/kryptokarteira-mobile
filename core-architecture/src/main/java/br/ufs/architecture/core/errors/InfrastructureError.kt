package br.ufs.architecture.core.errors

/**
 *
 * Created by @ubiratanfsoares
 *
 */

sealed class InfrastructureError : Throwable() {

    object RemoteSystemDown : InfrastructureError()
    object UndesiredResponse : InfrastructureError()
}