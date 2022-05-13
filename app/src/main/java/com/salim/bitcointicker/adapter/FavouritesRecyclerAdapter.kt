package com.salim.bitcointicker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.salim.bitcointicker.databinding.ItemFavouritesBinding
import com.salim.bitcointicker.network.dto.favourites.FavouriteCoins
import javax.inject.Inject

class FavouritesRecyclerAdapter @Inject constructor(
    val glide: RequestManager
): RecyclerView.Adapter<FavouritesRecyclerAdapter.ViewHolder>() {

    var favouriteLists: List<FavouriteCoins> = ArrayList()

    private var onItemClickListener: ((FavouriteCoins) -> Unit) ? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemFavouritesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    fun setOnItemClickListener(listener: (FavouriteCoins) -> Unit) {
        onItemClickListener = listener
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favourites = favouriteLists[position]
        favourites.let {
            holder.apply {
                bind(favourites)
            }
        }
    }

    override fun getItemCount(): Int = favouriteLists.size

    fun submitList(coinLists: List<FavouriteCoins>){
        favouriteLists = coinLists
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemFavouritesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FavouriteCoins) {
            binding.apply {
                favouriteCoin = item
            }
        }
    }
}