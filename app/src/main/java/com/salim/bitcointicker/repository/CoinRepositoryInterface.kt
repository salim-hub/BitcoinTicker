package com.salim.bitcointicker.repository

import com.salim.bitcointicker.network.dto.coin.Coin
import com.salim.bitcointicker.network.dto.details.Details
import retrofit2.Response

interface CoinRepositoryInterface {

    suspend fun fetchCoins(): Response<Coin>

    suspend fun fetchCoinDetails(coinId: String): Response<Details>

}