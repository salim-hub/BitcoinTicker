package com.salim.bitcointicker.viewmodel

import androidx.lifecycle.*
import com.salim.bitcointicker.repository.FavouritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repository: FavouritesRepository
): ViewModel() {

    val favCoinsLiveData = liveData(Dispatchers.IO) {
        emit(repository.getFavouriteCoinListFromFirestore())
    }

    fun deleteCoin(coinId: String) = liveData(Dispatchers.IO) {
        emit(repository.deleteCoinInFirestore(coinId))
    }
}