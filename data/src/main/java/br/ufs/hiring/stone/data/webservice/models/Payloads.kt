package br.ufs.hiring.stone.data.webservice.models

import br.ufs.hiring.stone.data.storage.WalletOwner
import com.google.gson.annotations.SerializedName

/**
 *
 * Created by @ubiratanfsoares
 *
 */

class NewWalletPayload(val owner: WalletOwner)

class HomePayload(
        val currencies: List<CurrencyPayload>,
        val broking: List<BrokingPayload>,
        val wallet: WalletPayload
)

class CurrencyPayload(val label: String, val name: String)

class BrokingPayload(
        val label: String,
        @SerializedName("buy_price") val buyPrice: Float,
        @SerializedName("selling_price") val sellPrice: Float
)

class WalletPayload(
        @SerializedName("owner") val ownerId: String,
        val savings: List<SavingPayload>,
        val transactions: List<TransactionPayload>
)

class SavingPayload(val label: String, val amount: Float)

class TransactionPayload(
        val type: String,
        val currency: String,
        val amount: Float,
        val timestamp: String
)

class TransactionResultPayload

