package com.salim.bitcointicker.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.salim.bitcointicker.network.dto.favourites.FavouriteCoins
import com.salim.bitcointicker.utils.Constants.FAVOURITES
import com.salim.bitcointicker.utils.DataOrException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouritesRepository @Inject constructor(
    private val favouritesRef: FirebaseFirestore
) {

    suspend fun getFavouriteCoinListFromFirestore(): DataOrException<MutableList<FavouriteCoins>, Exception> {
        val dataOrException = DataOrException<MutableList<FavouriteCoins>, Exception>()
        try {
            val productList = mutableListOf<FavouriteCoins>()
            favouritesRef.collection(FAVOURITES).document(Firebase.auth.currentUser!!.uid)
                .collection("saved_coins").get().addOnSuccessListener {
                    for (i in it) {
                        i.toObject(FavouriteCoins::class.java).let {
                            productList.add(it)
                        }
                    }
                }.addOnFailureListener {

                }.await()
            dataOrException.data = productList
        } catch (e: FirebaseFirestoreException) {
            dataOrException.e = e
        }
        return dataOrException
    }

    fun deleteCoinInFirestore(coinId: String): DataOrException<Boolean, Exception> {
        val dataOrException = DataOrException<Boolean, Exception>()
        try {
            favouritesRef.collection(FAVOURITES).document(Firebase.auth.currentUser!!.uid)
                .collection("saved_coins")
                .document(coinId).delete()
            dataOrException.data = true
        } catch (e: FirebaseFirestoreException) {
            dataOrException.e = e
        }
        return dataOrException
    }
}