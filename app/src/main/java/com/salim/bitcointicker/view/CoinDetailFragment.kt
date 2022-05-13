package com.salim.bitcointicker.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.salim.bitcointicker.R
import com.salim.bitcointicker.adapter.DetailRecyclerAdapter
import com.salim.bitcointicker.adapter.PaginationScrollListener
import com.salim.bitcointicker.databinding.FragmentCoinDetailBinding
import com.salim.bitcointicker.network.dto.favourites.FavouriteCoins
import com.salim.bitcointicker.network.dto.details.Details
import com.salim.bitcointicker.utils.Constants.FAVOURITES
import com.salim.bitcointicker.utils.Status
import com.salim.bitcointicker.viewmodel.CoinViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class CoinDetailFragment @Inject constructor(
    private val detailRecyclerAdapter: DetailRecyclerAdapter
): Fragment(R.layout.fragment_coin_detail) {

    private val TAG = "CoinDetailFragment"
    private var fragmentBinding: FragmentCoinDetailBinding? = null
    private lateinit var coinViewModel: CoinViewModel
    private var coinId: String? = null
    private var coinImage: String? = null
    private var coinSymbol: String? = null
    private var coinPrice: String? = null
    private var coinDetailList: ArrayList<Details> = ArrayList()
    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewLifecycleOwner.lifecycleScope.launch {
            checkFavorite()
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coinViewModel = ViewModelProvider(requireActivity())[CoinViewModel::class.java]

        val binding = FragmentCoinDetailBinding.bind(view)
        fragmentBinding = binding

        binding.detailsRecyclerView.setHasFixedSize(true)
        binding.detailsRecyclerView.adapter = detailRecyclerAdapter
        binding.detailsRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.detailsRecyclerView.addOnScrollListener(object: PaginationScrollListener(
            LinearLayoutManager(requireActivity())
        ){
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                loadMoreItems()
                getMoreItems()
            }
        })

        binding.swipeToRefresh.setProgressBackgroundColorSchemeColor(Color.WHITE)
        binding.swipeToRefresh.setColorSchemeColors(Color.BLACK)
        binding.swipeToRefresh.setOnRefreshListener {
            observeUI()
            binding.swipeToRefresh.isRefreshing = false
        }

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.coinId = pref.getString("coinId", "none")
            this.coinSymbol = pref.getString("coinSymbol", "none")
            this.coinImage = pref.getString("coinImage", "none")
            this.coinPrice = pref.getString("coinPrice", "none")
        }

        coinViewModel.getCoinDetails(coinId.toString())
        observeUI()

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.favouriteIcon.setOnClickListener {
            val addedCoin = FavouriteCoins(coinId, coinImage, coinSymbol, coinPrice)
            viewLifecycleOwner.lifecycleScope.launch {
                coinViewModel.saveFavourite(addedCoin)
                checkFavorite()
            }
            binding.favouriteIcon.setImageResource(R.drawable.ic_favourite_selected)
        }
    }

    private fun getMoreItems() {
        isLoading = false
        detailRecyclerAdapter.addData(coinDetailList)
    }

    private fun observeUI() {
        coinViewModel.coinDetails.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {
                    fragmentBinding?.progressBar?.visibility = View.GONE
                    val data = it.data!!
                    detailRecyclerAdapter.submitList(listOf(data))
                }
                Status.ERROR -> {
                    fragmentBinding?.progressBar?.visibility = View.GONE
                    it.message?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    }
                }
                Status.LOADING -> {
                    fragmentBinding?.progressBar?.visibility = View.VISIBLE
                }
            }
        }
    }

    private suspend fun checkFavorite() {
        val db = Firebase.firestore.collection(FAVOURITES)
            .document(Firebase.auth.currentUser!!.uid)

        db.collection("saved_coins").document(coinId.toString()).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    fragmentBinding?.favouriteIcon?.setImageResource(R.drawable.ic_favourite_selected)
                } else {
                    fragmentBinding?.favouriteIcon?.setImageResource(R.drawable.ic_favourite_unselected)
                }
            }.addOnFailureListener {

            }.await()
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}