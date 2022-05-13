package com.salim.bitcointicker.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.salim.bitcointicker.adapter.CoinRecyclerAdapter
import com.salim.bitcointicker.adapter.DetailRecyclerAdapter
import com.salim.bitcointicker.adapter.FavouritesRecyclerAdapter
import com.salim.bitcointicker.adapter.SearchRecyclerAdapter
import javax.inject.Inject

class CoinFragmentFactory @Inject constructor(
    private val coinRecyclerAdapter: CoinRecyclerAdapter,
    private val detailRecyclerAdapter: DetailRecyclerAdapter,
    private val favouritesRecyclerAdapter: FavouritesRecyclerAdapter,
    private val searchRecyclerAdapter: SearchRecyclerAdapter

): FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {

        return when(className) {
            CoinFragment::class.java.name -> CoinFragment(coinRecyclerAdapter)
            CoinDetailFragment::class.java.name -> CoinDetailFragment(detailRecyclerAdapter)
            FavouriteCoinsFragment::class.java.name -> FavouriteCoinsFragment(favouritesRecyclerAdapter)
            SearchFragment::class.java.name -> SearchFragment(searchRecyclerAdapter)
            else -> super.instantiate(classLoader, className)
        }
    }
}