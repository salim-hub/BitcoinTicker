package com.salim.bitcointicker.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.salim.bitcointicker.network.dto.coin.CoinItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinDao {
    @Query("SELECT * FROM coins")
    fun getAllCoins(): LiveData<List<CoinItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(coins: CoinItem)

    @Query("DELETE FROM coins")
    fun deleteAllCoins()

    @Query("SELECT * FROM coins ORDER BY id ASC")
    fun readData(): Flow<List<CoinItem>>

    @Query("SELECT * FROM coins WHERE id LIKE :searchQuery OR symbol LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): Flow<List<CoinItem>>

}