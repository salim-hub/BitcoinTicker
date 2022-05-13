package com.salim.bitcointicker.network.api

import com.salim.bitcointicker.network.dto.coin.Coin
import com.salim.bitcointicker.network.dto.details.Details
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CoinApiService {

    @GET("/api/v3/coins/markets?vs_currency=usd")
    suspend fun getCoins(): Response<Coin>

    @GET("api/v3/coins/{coinId}")
    suspend fun getCoinDetails(
        @Path("coinId") coinId: String
    ): Response<Details>

    @GET("/api/v3/coins/markets?vs_currency=usd")
    fun getDataFromAPI(): Call<Coin>
}
