package br.ufs.rxmvvm.core.errors

/**
 *
 * Created by @ubiratanfsoares
 *
 */

sealed class InfrastructureError : Throwable() {

    object RemoteSystemDown : InfrastructureError()
    object UndesiredResponse : InfrastructureError()
}