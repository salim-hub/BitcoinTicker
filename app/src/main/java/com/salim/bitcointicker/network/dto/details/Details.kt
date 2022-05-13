package com.salim.bitcointicker.network.dto.details

import com.google.gson.annotations.SerializedName

data class Details(
    @SerializedName("description")
    val description: Description,
    @SerializedName("hashing_algorithm")
    val hashingAlgorithm: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("image")
    val image: Image,
    @SerializedName("market_cap_rank")
    val marketCapRank: Int,
    @SerializedName("market_data")
    val marketData: MarketData,
    @SerializedName("name")
    val name: String,
    @SerializedName("symbol")
    val symbol: String
)

data class Description(
    @SerializedName("en")
    val en: String,
)

data class CurrentPrice(
    @SerializedName("usd")
    val usd: Double
)

data class Image(
    @SerializedName("large")
    val large: String,
    @SerializedName("small")
    val small: String,
    @SerializedName("thumb")
    val thumb: String
)

data class MarketCapChange24hInCurrency(
    @SerializedName("usd")
    val usd: Double
)

data class PriceChangePercentage24hInCurrency(
    @SerializedName("usd")
    val usd: Double
)

data class PriceChange24hInCurrency(
    @SerializedName("usd")
    val usd: Double
)

data class MarketCapChangePercentage24hInCurrency(
    @SerializedName("usd")
    val usd: Double
)

data class MarketData(
    @SerializedName("current_price")
    val currentPrice: CurrentPrice,
    @SerializedName("last_updated")
    val lastUpdated: String,
    @SerializedName("market_cap")
    val marketCap: MarketCap,
    @SerializedName("market_cap_change_24h")
    val marketCapChange24h: Double,
    @SerializedName("market_cap_change_24h_in_currency")
    val marketCapChange24hInCurrency: MarketCapChange24hInCurrency,
    @SerializedName("market_cap_change_percentage_24h")
    val marketCapChangePercentage24h: Double,
    @SerializedName("market_cap_change_percentage_24h_in_currency")
    val marketCapChangePercentage24hInCurrency: MarketCapChangePercentage24hInCurrency,
    @SerializedName("market_cap_rank")
    val marketCapRank: Int,
    @SerializedName("max_supply")
    val maxSupply: Int,
    @SerializedName("mcap_to_tvl_ratio")
    val mcapToTvlRatio: Any,
    @SerializedName("price_change_24h")
    val priceChange24h: Double,
    @SerializedName("price_change_24h_in_currency")
    val priceChange24hInCurrency: PriceChange24hInCurrency,
    @SerializedName("price_change_percentage_14d")
    val priceChangePercentage14d: Double,
    @SerializedName("price_change_percentage_1y")
    val priceChangePercentage1y: Double,
    @SerializedName("price_change_percentage_200d")
    val priceChangePercentage200d: Double,
    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Double,
    @SerializedName("price_change_percentage_24h_in_currency")
    val priceChangePercentage24hInCurrency: PriceChangePercentage24hInCurrency
)

data class MarketCap(
    @SerializedName("usd")
    val usd: Long
)