package br.ufs.hiring.stone.domain

import java.lang.IllegalArgumentException

/**
 *
 * Created by @ubiratanfsoares
 *
 */

sealed class Currency(val label: String, val name: String) {
    object Brita : Currency("bta", "Brita")
    object Bitcoin : Currency("btc", "Bitcoin")
    object Real : Currency("blr", "Real")

    companion object From {
        operator fun invoke(label: String) = when (label) {
            "bta" -> Brita
            "btc" -> Bitcoin
            "blr" -> Real
            else -> throw IllegalArgumentException("Unknow label")
        }
    }
}