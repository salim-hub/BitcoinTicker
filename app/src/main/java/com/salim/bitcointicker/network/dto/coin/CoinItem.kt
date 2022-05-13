package com.salim.bitcointicker.network.dto.coin

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "coins")
data class CoinItem(
    @SerializedName("current_price")
    @ColumnInfo(name = "current_price")
    val currentPrice: Double,
    @ColumnInfo(name="id")
    @SerializedName("id")
    val id: String,
    @SerializedName("image")
    @ColumnInfo(name="image")
    val image: String,
    @SerializedName("last_updated")
    @ColumnInfo(name="last_updated")
    val lastUpdated: String,
    @SerializedName("market_cap")
    @ColumnInfo(name="market_cap")
    val marketCap: Long,
    @SerializedName("market_cap_change_24h")
    @ColumnInfo(name="market_cap_change_24h")
    val marketCapChange24h: Double,
    @SerializedName("market_cap_change_percentage_24h")
    @ColumnInfo(name="market_cap_change_percentage_24h")
    val marketCapChangePercentage24h: Double,
    @SerializedName("market_cap_rank")
    @ColumnInfo(name="market_cap_rank")
    val marketCapRank: Int,
    @SerializedName("name")
    @ColumnInfo(name="name")
    val name: String,
    @SerializedName("price_change_24h")
    @ColumnInfo(name="price_change_24h")
    val priceChange24h: Double,
    @SerializedName("price_change_percentage_24h")
    @ColumnInfo(name="price_change_percentage_24h")
    val priceChangePercentage24h: Double,
    @SerializedName("symbol")
    @ColumnInfo(name="symbol")
    val symbol: String
) {
    @PrimaryKey(autoGenerate = true)
    var uuid: Int = 0
}