package br.ufs.rxmvvm.core.errors

/**
 *
 * Created by @ubiratanfsoares
 *
 */

sealed class NetworkingIssue : Throwable() {
    object InternetUnreachable : NetworkingIssue()
    object OperationTimeout : NetworkingIssue()
    object ConnectionSpike : NetworkingIssue()
}