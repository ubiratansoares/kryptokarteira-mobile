package br.ufs.hiring.stone.domain

import io.reactivex.Completable

/**
 *
 * Created by @ubiratanfsoares
 *
 */

interface NewTrasaction {

    fun perform(desired: Transaction): Completable

}