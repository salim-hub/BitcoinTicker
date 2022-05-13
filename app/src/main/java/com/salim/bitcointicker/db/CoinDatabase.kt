package com.salim.bitcointicker.db

import android.content.Context
import androidx.room.*
import com.salim.bitcointicker.network.dto.coin.CoinItem

@Database(entities = [CoinItem::class], version = 1, exportSchema = false)
abstract class CoinDatabase: RoomDatabase() {
    abstract fun coinDao(): CoinDao

    companion object {

        private var INSTANCE: CoinDatabase? = null

        fun getDatabase(context: Context): CoinDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    CoinDatabase::class.java,
                    "coins"
                ).allowMainThreadQueries().build()
            }

            return INSTANCE!!
        }
    }
}