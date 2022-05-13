package com.salim.bitcointicker.view

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.salim.bitcointicker.adapter.FavouritesRecyclerAdapter
import com.salim.bitcointicker.databinding.FragmentFavouriteCoinsBinding
import com.salim.bitcointicker.network.dto.favourites.FavouriteCoins
import com.salim.bitcointicker.viewmodel.FavouritesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavouriteCoinsFragment @Inject constructor(
    private val favouritesRecyclerAdapter: FavouritesRecyclerAdapter
) : Fragment() {

    private val favouritesViewModel: FavouritesViewModel by viewModels()
    private val favCoins: MutableList<FavouriteCoins> = mutableListOf()
    private var _binding: FragmentFavouriteCoinsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouriteCoinsBinding.inflate(inflater, container, false)
        binding.favouriteCoinsRecyclerView.adapter = favouritesRecyclerAdapter
        getFavouritesList()

        setOnRecyclerViewItemSwipedListener()

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
        val view = binding.root
        return view
    }

    private fun getFavouritesList() {
        displayProgressBar()
        favouritesViewModel.favCoinsLiveData.observe(requireActivity()) { dataOrException ->
            val favList = dataOrException.data
            if (favList != null) {
                if (favCoins.isNotEmpty()) {
                    favList.clear()
                }
                favCoins.addAll(favList)
                favouritesRecyclerAdapter.submitList(favCoins)
                favouritesRecyclerAdapter.notifyDataSetChanged()
                hideProgressBar()
            }

            if (dataOrException.e != null) {
                Toast.makeText(requireActivity(), dataOrException.e!!.message!!, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun setOnRecyclerViewItemSwipedListener() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                val swipedCoin = favCoins[position]
                deleteFavCoin(position, swipedCoin)
            }

        }).attachToRecyclerView(binding.favouriteCoinsRecyclerView)
    }

    private fun deleteFavCoin(position: Int, favCoin: FavouriteCoins) {
        displayProgressBar()
        val isFavCoinDeletedLiveData = favouritesViewModel.deleteCoin(favCoin.coinName.toString())
        isFavCoinDeletedLiveData.observe(this) { dataOrException ->
            val isProductDeleted = dataOrException.data
            if (isProductDeleted != null) {
                if (isProductDeleted) {
                    favCoins.removeAt(position)
                    favouritesRecyclerAdapter.notifyItemRemoved(position)
                    favouritesRecyclerAdapter.notifyItemRangeChanged(position, favCoins.size)
                    hideProgressBar()
                }
            }

            if (dataOrException.e != null) {
                Toast.makeText(requireActivity(), dataOrException.e!!.message!!, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun displayProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}