package com.salim.bitcointicker.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.salim.bitcointicker.network.dto.coin.Coin
import com.salim.bitcointicker.network.dto.coin.CoinItem
import com.salim.bitcointicker.network.dto.favourites.FavouriteCoins
import com.salim.bitcointicker.network.dto.details.Details
import com.salim.bitcointicker.repository.CoinRepository
import com.salim.bitcointicker.utils.Resource
import com.salim.bitcointicker.utils.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CoinViewModel @Inject constructor(
    private val coinRepository: CoinRepository,
    @ApplicationContext private val context: Context
): ViewModel() {

    val coinList: MutableLiveData<Resource<Coin>> = MutableLiveData()
    val coinDetails: MutableLiveData<Resource<Details>> = MutableLiveData()

    val coins: MutableLiveData<Resource<Coin>> = MutableLiveData()
    val coinsList: LiveData<Resource<Coin>>
        get() = coins

    val readData = coinRepository.readData().asLiveData()


    suspend fun saveFavourite(coinId: FavouriteCoins) {
        coinRepository.addCoinToFavourites(coinId)
    }

    fun searchDatabase(searchQuery: String): LiveData<List<CoinItem>> {
        return coinRepository.searchDatabase(searchQuery).asLiveData()
    }

    suspend fun getAllCoinList(): LiveData<List<CoinItem>> {
        return coinRepository.getAllCoins()
    }

    suspend fun makeApiCall() {
        coinRepository.makeApiCall()
    }

    fun fetchCoinList() {
        coinList.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                if (hasInternetConnection(context)) {
                    val response = coinRepository.fetchCoins()
                    coinList.postValue(Resource.success(response.body()!!))
                } else {
                    coinList.postValue(Resource.error("Internet Connection Error", null))
                }
            } catch (e: Exception) {
                when (e) {
                    is IOException -> {
                        coinList.postValue(Resource.error("Network Failure" + e.localizedMessage, null))
                    }
                    else -> {
                        coinList.postValue(Resource.error("Conversion Error", null))
                    }
                }
            }
        }
    }

    fun getCoinDetails(coinId: String) {
        coinDetails.postValue(Resource.loading(null))
        viewModelScope.launch {
            try {
                if (hasInternetConnection(context)) {
                    val detailResponse = coinRepository.fetchCoinDetails(coinId)
                    coinDetails.postValue(Resource.success(detailResponse.body()!!))
                } else {
                    coinDetails.postValue(Resource.error("Internet Connection Error", null))
                }
            } catch (e: Exception) {
                when (e) {
                    is IOException -> {
                        coinDetails.postValue(Resource.error("Network Failure" + e.localizedMessage, null))
                    }
                    else -> {
                        coinDetails.postValue(Resource.error("Conversion Error" + e.localizedMessage, null))
                    }
                }
            }
        }
    }
}

