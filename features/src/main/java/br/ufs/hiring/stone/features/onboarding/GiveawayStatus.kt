package br.ufs.hiring.stone.features.onboarding

/**
 *
 * Created by @ubiratanfsoares
 *
 */

sealed class GiveawayStatus {
    object Available : GiveawayStatus()
    object Received : GiveawayStatus()
}