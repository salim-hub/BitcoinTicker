package com.salim.bitcointicker.network.dto.favourites

data class FavouriteCoins(
    var coinName: String? = "",
    var coinImage: String? = "",
    var coinSymbol: String? = "",
    var coinPrice: String? = ""
)