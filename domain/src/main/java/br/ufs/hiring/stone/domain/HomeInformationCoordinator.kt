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

        val cached = persister.previousInformation()
        val refreshed = updater.execute()

        return cached
                .concatWith(refreshed)
                .doOnNext { persister.save(it) }
    }
}