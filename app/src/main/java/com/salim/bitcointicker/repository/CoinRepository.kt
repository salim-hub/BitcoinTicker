package com.salim.bitcointicker.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.salim.bitcointicker.db.CoinDao
import com.salim.bitcointicker.network.dto.coin.Coin
import com.salim.bitcointicker.network.dto.coin.CoinItem
import com.salim.bitcointicker.network.dto.favourites.FavouriteCoins
import com.salim.bitcointicker.network.dto.details.Details
import com.salim.bitcointicker.network.api.CoinApiService
import com.salim.bitcointicker.utils.Constants.FAVOURITES
import com.salim.bitcointicker.utils.DataOrException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinRepository @Inject constructor(
    private val apiService: CoinApiService,
    private val coinDao: CoinDao,
): CoinRepositoryInterface {

    suspend fun getAllCoins(): LiveData<List<CoinItem>> = withContext(Dispatchers.IO){
        coinDao.getAllCoins()
    }

    fun insertCoins(coins: CoinItem) {
        coinDao.insertAll(coins)
    }

    fun readData(): Flow<List<CoinItem>> {
        return coinDao.readData()
    }

    fun searchDatabase(searchQuery: String): Flow<List<CoinItem>> {
        return coinDao.searchDatabase(searchQuery)
    }

    suspend fun makeApiCall() = withContext(Dispatchers.IO) {
        val call: Call<Coin> = apiService.getDataFromAPI()
        call.enqueue(object: Callback<Coin> {
            override fun onResponse(call: Call<Coin>, response: Response<Coin>) {
                if (response.isSuccessful) {
                    CoroutineScope(Dispatchers.IO).launch {
                        coinDao.deleteAllCoins()
                        response.body()?.forEach {
                            insertCoins(it)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Coin>, t: Throwable) {

            }
        })
    }

    override suspend fun fetchCoins(): Response<Coin> = withContext(Dispatchers.IO) {
        val coinList = apiService.getCoins()
        coinList
    }

    override suspend fun fetchCoinDetails(coinId: String): Response<Details> = withContext(Dispatchers.IO) {
        val coinDetails = apiService.getCoinDetails(coinId)
        coinDetails
    }

    suspend fun addCoinToFavourites(coin: FavouriteCoins): DataOrException<Boolean, Exception> {
        val dataOrException = DataOrException<Boolean, Exception>()
        val db = Firebase.firestore
            .collection(FAVOURITES)
            .document(Firebase.auth.currentUser!!.uid)

        try {
            db.collection("saved_coins").document(coin.coinName.toString()).get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val query: Query = db.collection("saved_coins")
                            .whereEqualTo("coinName", coin.coinName.toString())
                        query.get()
                            .addOnCompleteListener { p0 ->
                                if (p0.isSuccessful) {
                                    for (document in p0.result!!) {
                                        document.reference.delete()
                                            .addOnSuccessListener {
                                                Log.d("TAG", "Document successfully deleted!")
                                            }
                                    }
                                } else {
                                    Log.w("TAG", "Error getting documents: ", p0.exception)
                                }
                            }.addOnFailureListener { p0 ->
                                Log.w("TAG", "Error deleting document", p0)
                            }
                    } else {
                        db.collection("saved_coins").document(coin.coinName.toString())
                            .set(coin, SetOptions.merge()).addOnSuccessListener {
                                Log.d("TAG", "Document ADDED")
                            }
                    }
                }.addOnFailureListener {

            }.await()

            dataOrException.data = true
        } catch (e: FirebaseFirestoreException) {
            dataOrException.e = e
        }
        return dataOrException
    }
}