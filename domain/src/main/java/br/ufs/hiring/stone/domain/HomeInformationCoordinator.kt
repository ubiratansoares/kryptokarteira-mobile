package br.ufs.hiring.stone.domain

import io.reactivex.Observable

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class HomeInformationCoordinator(
        private val persister: OfflineHomeSupport,
        private val updater: RetrieveHomeInformation) {

    fun lastInformationAvailable(): Observable<HomeInformation> {

        val cached = persister
                .previousInformation()
                .onErrorResumeNext(Observable.empty())

        val refreshed = updater
                .execute()
                .doOnNext { persister.save(it) }

        return cached.concatWith(refreshed)

    }
}