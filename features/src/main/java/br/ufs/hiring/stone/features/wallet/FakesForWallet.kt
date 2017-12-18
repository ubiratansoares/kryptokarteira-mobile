package br.ufs.hiring.stone.features.wallet

/**
 *
 * Created by @ubiratanfsoares
 *
 */


val fakes = listOf(
        Headline(
                type = HeadlineType.Block,
                headline = "Seus investimentos"
        ),
        CardHeader(),
        Investiment("Em Reais", "34558,20 BLR"),
        Investiment("Em Bitcoins", "1,20 BTC"),
        Investiment("Em Britas", "1234,56 BTA"),
        CardFooter(),
        Headline(
                type = HeadlineType.Block,
                headline = "Últimas cotações"
        ),
        CardHeader(),
        Headline(type = HeadlineType.Trade, headline = "Bitcoin"),
        TradeValue("Preço de compra", "R$ 56732"),
        TradeValue("Preço de venda", "R$ 55999"),
        IntraCardSpace(),
        HorizontalLine(),
        CallToAction(),
        CardFooter(),
        BetweenCardsSpace(),
        CardHeader(),
        Headline(type = HeadlineType.Trade, headline = "Brita"),
        TradeValue("Preço de compra", "R$ 3.14"),
        TradeValue("Preço de venda", "R$ 3.12"),
        IntraCardSpace(),
        HorizontalLine(),
        CallToAction(),
        CardFooter(),
        Headline(
                type = HeadlineType.Block,
                headline = "Suas movimentações"
        ),
        CardHeader(),
        TransactionEntry(
                type = EntryType.Header,
                currency = "Bitcion",
                transcationType = "COMPRA",
                formattedDate = "em 28/02/2017",
                formattedTotal = "1 BTC"
        ),
        TransactionEntry(
                type = EntryType.Middle,
                currency = "Brita",
                transcationType = "VENDA",
                formattedDate = "em 28/02/2017",
                formattedTotal = "567 BTA"
        ),
        TransactionEntry(
                type = EntryType.Middle,
                currency = "Brita",
                transcationType = "VENDA",
                formattedDate = "em 27/02/2017",
                formattedTotal = "400 BTA"
        ),
        TransactionEntry(
                type = EntryType.Footer,
                currency = "Bitcoin",
                transcationType = "VENDA",
                formattedDate = "em 26/02/2017",
                formattedTotal = "2 BTC"
        ),
        CardFooter()
)
