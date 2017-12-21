package br.ufs.hiring.stone.domain

/**
 *
 * Created by @ubiratanfsoares
 *
 */


data class HomeInformation(
        val brokings: List<BrokingInformation>,
        val savings: List<Saving>,
        val transactions: List<Transaction>
)

