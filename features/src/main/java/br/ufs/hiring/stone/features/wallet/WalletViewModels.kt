package br.ufs.hiring.stone.features.wallet

/**
 *
 * Created by @ubiratanfsoares
 *
 */

sealed class EntryModel

class CallToAction : EntryModel()
class Investiment(val formattedName: String, val formattedValue: String) : EntryModel()
class TradeValue(val formattedOperation: String, val formattedValue: String) : EntryModel()
class CardHeader : EntryModel()
class CardFooter : EntryModel()
class IntraCardSpace : EntryModel()
class BetweenCardsSpace : EntryModel()
class HorizontalLine : EntryModel()

class TransactionEntry(
        val type: EntryType,
        val currency: String,
        val transcationType: String,
        val formattedTotal: String,
        val formattedDate: String) : EntryModel()

sealed class EntryType {
    object Middle : EntryType()
    object Footer : EntryType()
    object Header : EntryType()
    object Standalone : EntryType()
}

class Headline(val type : HeadlineType, val headline: String) : EntryModel()

sealed class HeadlineType {
    object Block : HeadlineType()
    object Trade : HeadlineType()
}