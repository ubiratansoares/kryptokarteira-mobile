package br.ufs.hiring.stone.data.webservice.models

import com.google.gson.annotations.SerializedName

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class NewTransactionBody(
        @SerializedName("type") val operationType: String,
        val currency: String,
        @SerializedName("quantity") val quantity: Float
)