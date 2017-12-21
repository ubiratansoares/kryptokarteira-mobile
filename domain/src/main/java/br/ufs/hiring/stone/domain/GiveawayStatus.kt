package br.ufs.hiring.stone.domain

/**
 *
 * Created by @ubiratanfsoares
 *
 */

sealed class GiveawayStatus {
    object Available : GiveawayStatus()
    object Received : GiveawayStatus()
}