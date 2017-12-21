package br.ufs.hiring.stone.domain

import io.reactivex.Observable

/**
 *
 * Created by @ubiratanfsoares
 *
 */
interface OfflineHomeSupport {

    fun previousInformation(): Observable<HomeInformation>

    fun save(info: HomeInformation)

}