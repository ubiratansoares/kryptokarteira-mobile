package br.ufs.hiring.stone.domain

import io.reactivex.Observable

/**
 *
 * Created by @ubiratanfsoares
 *
 */

interface RetrieveHomeInformation {

    fun execute(): Observable<HomeInformation>

}