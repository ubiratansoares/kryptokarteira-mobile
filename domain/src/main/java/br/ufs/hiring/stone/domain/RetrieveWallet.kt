package br.ufs.hiring.stone.domain

import io.reactivex.Observable

/**
 *
 * Created by @ubiratanfsoares
 *
 */

interface RetrieveWallet {

    fun execute(): Observable<HomeInformation>

}