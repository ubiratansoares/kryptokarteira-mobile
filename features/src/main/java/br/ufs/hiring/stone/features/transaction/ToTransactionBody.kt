package br.ufs.hiring.stone.features.transaction

import br.ufs.hiring.stone.data.webservice.models.NewTransactionBody
import br.ufs.hiring.stone.domain.Transaction

/**
 *
 * Created by @ubiratanfsoares
 *
 */

object ToTransactionBody {

    operator fun invoke(target: Transaction): NewTransactionBody {
        return with(target) {
            NewTransactionBody(
                    operationType = target.type.toString(),
                    currency = target.currency.label,
                    quantity = target.amount
            )
        }
    }
}