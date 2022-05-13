package com.salim.bitcointicker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.salim.bitcointicker.R
import com.salim.bitcointicker.network.dto.coin.CoinItem
import javax.inject.Inject

class SearchRecyclerAdapter @Inject constructor(
    val glide: RequestManager
): RecyclerView.Adapter<SearchRecyclerAdapter.SearchViewHolder>() {

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var onItemClickListener: ((CoinItem) -> Unit)? = null

    private val diffUtil = object : DiffUtil.ItemCallback<CoinItem>() {
        override fun areItemsTheSame(oldItem: CoinItem, newItem: CoinItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CoinItem, newItem: CoinItem): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var coins: List<CoinItem>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search, parent, false)
        return SearchViewHolder(view)
    }

    fun setOnItemClickListener(listener: (CoinItem) -> Unit) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val searchImageView = holder.itemView.findViewById<ImageView>(R.id.searchIV)
        val searchTextView = holder.itemView.findViewById<TextView>(R.id.searchTV)
        val coinItem = coins[position]
        holder.itemView.apply {
            glide.load(coinItem.image).into(searchImageView)
            searchTextView.text = coinItem.name
            setOnClickListener {
                onItemClickListener?.let {
                    it(coinItem)
                    val pref = holder.itemView.context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
                    pref?.putString("coinId", coinItem.id)
                    pref?.putString("coinSymbol", coinItem.symbol)
                    pref?.putString("coinImage", coinItem.image)
                    pref?.putString("coinPrice", coinItem.currentPrice.toString())
                    pref?.apply()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return coins.size
    }

    fun submitList(coinLists: List<CoinItem>){
        coins = coinLists
        notifyDataSetChanged()
    }
}