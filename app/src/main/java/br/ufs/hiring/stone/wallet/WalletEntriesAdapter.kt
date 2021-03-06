package br.ufs.hiring.stone.wallet

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.ufs.hiring.stone.R
import br.ufs.hiring.stone.domain.TransactionType.Buy
import br.ufs.hiring.stone.domain.TransactionType.Sell
import br.ufs.hiring.stone.features.wallet.*
import br.ufs.hiring.stone.features.wallet.EntryType.*
import br.ufs.hiring.stone.features.wallet.HeadlineType.Block
import br.ufs.hiring.stone.features.wallet.HeadlineType.Trade
import br.ufs.hiring.stone.transaction.NewTransactionActivity
import kotlinx.android.synthetic.main.view_entry_block_headline.view.*
import kotlinx.android.synthetic.main.view_entry_calltoactions.view.*
import kotlinx.android.synthetic.main.view_entry_investment.view.*
import kotlinx.android.synthetic.main.view_entry_trade_value.view.*
import kotlinx.android.synthetic.main.view_entry_transaction_middle.view.*


/**
 *
 * Created by @ubiratanfsoares
 *
 */

typealias Holder = RecyclerView.ViewHolder

class WalletEntriesAdapter : RecyclerView.Adapter<Holder>() {

    private val models: MutableList<EntryModel> = ArrayList()

    fun addModel(toAdd: EntryModel) {
        models += toAdd
        notifyDataSetChanged()
    }

    fun addModels(entries: List<EntryModel>) {
        models.addAll(entries)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return models.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layout = layoutForViewType(viewType)
        val inflater = LayoutInflater.from(parent.context)
        val root = inflater.inflate(layout, parent, false)
        return viewHolderForType(root, viewType)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val model = models[position]
        when (holder) {
            is HeadlineHolder -> holder.bind(model as Headline)
            is InvestimentHolder -> holder.bind(model as Investiment)
            is TradeValueHolder -> holder.bind(model as TradeValue)
            is TransactionHolder -> holder.bind(model as TransactionEntry)
            is CallToActionHolder -> holder.bind(model as CallToAction)
            is NoData -> Unit
            else -> throw IllegalArgumentException("Invalid ViewType for position")
        }
    }

    override fun getItemViewType(position: Int): Int {
        val model = models[position]

        return when (model) {

            is CallToAction -> 0
            is Investiment -> 2
            is TradeValue -> 4

            is Headline -> when (model.type) {
                is Block -> 1
                is Trade -> 3
            }

            is TransactionEntry -> when (model.type) {
                is Footer -> 5
                is Header -> 6
                is Standalone -> 7
                is Middle -> 8
            }

            is CardHeader -> 9
            is CardFooter -> 10
            is HorizontalLine -> 11
            is IntraCardSpace -> 12
            is BetweenCardsSpace -> 13
            else -> throw IllegalArgumentException("Invalid ViewType for position")
        }
    }

    private fun viewHolderForType(root: View, viewType: Int): Holder {
        return when (viewType) {
            0 -> CallToActionHolder(root)
            1, 3 -> HeadlineHolder(root)
            2 -> InvestimentHolder(root)
            4 -> TradeValueHolder(root)
            5, 6, 7, 8 -> TransactionHolder(root)
            else -> NoData(root)
        }
    }

    private fun layoutForViewType(viewType: Int): Int {
        return when (viewType) {
            0 -> R.layout.view_entry_calltoactions
            1 -> R.layout.view_entry_block_headline
            2 -> R.layout.view_entry_investment
            3 -> R.layout.view_entry_trade_headline
            4 -> R.layout.view_entry_trade_value
            5 -> R.layout.view_entry_transaction_footer
            6 -> R.layout.view_entry_transaction_header
            7 -> R.layout.view_entry_transaction_standalone
            8 -> R.layout.view_entry_transaction_middle
            9 -> R.layout.view_entry_cardheader
            10 -> R.layout.view_entry_cardfooter
            11 -> R.layout.view_entry_line
            12 -> R.layout.view_entry_card_space
            13 -> R.layout.view_entry_space_between
            else -> throw IllegalArgumentException("Invalid ViewType ")
        }
    }

    fun clear() = models.clear()


}

class NoData(root: View) : Holder(root)

class HeadlineHolder(private val root: View) : Holder(root) {
    fun bind(model: Headline) {
        root.headline.text = model.headline
    }
}

class InvestimentHolder(private val root: View) : Holder(root) {
    fun bind(model: Investiment) {
        with(model) {
            root.investmentName.text = formattedName
            root.investmentValue.text = formattedValue
        }
    }
}

class TradeValueHolder(private val root: View) : Holder(root) {
    fun bind(model: TradeValue) {
        with(model) {
            root.operationName.text = formattedOperation
            root.operationValue.text = formattedValue
        }
    }
}

class TransactionHolder(private val root: View) : Holder(root) {
    fun bind(model: TransactionEntry) {
        with(model) {
            root.currencyName.text = currency
            root.transactionDate.text = formattedDate
            root.transactionType.text = transcationType
            root.transactionTotal.text = formattedTotal
        }
    }
}

class CallToActionHolder(private val root: View) : Holder(root) {

    fun bind(model: CallToAction) {

        val origin = root.context

        listOf(root.sellButton, root.buyButton)
                .forEach {

                    val operationType = when (it.id) {
                        R.id.buyButton -> Buy.toString()
                        R.id.sellButton -> Sell.toString()
                        else -> throw IllegalArgumentException("Not a call-to-action")
                    }

                    val toTransaction = NewTransactionActivity.launchIntent(
                            origin,
                            model.currencyLabel,
                            operationType
                    )

                    it.setOnClickListener { origin.startActivity(toTransaction) }
                }

    }
}

