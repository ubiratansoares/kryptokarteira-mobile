package br.ufs.architecture.core.errors

/**
 *
 * Created by @ubiratanfsoares
 *
 */

typealias StatusCode = Int

sealed class InfrastructureError : Throwable() {

    data class ClientIssue(val code: StatusCode) : InfrastructureError()
    object RemoteSystemDown : InfrastructureError()
    object UndesiredResponse : InfrastructureError()
    object StorageAccessError : InfrastructureError()
    object LocalDatabaseAccessError : InfrastructureError()
}