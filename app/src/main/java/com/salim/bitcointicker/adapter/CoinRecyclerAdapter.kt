package com.salim.bitcointicker.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.salim.bitcointicker.R
import com.salim.bitcointicker.databinding.ItemCoinListBinding
import com.salim.bitcointicker.network.dto.coin.CoinItem
import javax.inject.Inject

class CoinRecyclerAdapter @Inject constructor(
    val glide: RequestManager
): RecyclerView.Adapter<CoinRecyclerAdapter.ViewHolder>() {

    private lateinit var recyclerView: RecyclerView
    var coinList: List<CoinItem> = ArrayList()
    private var onItemClickListener: ((CoinItem) -> Unit) ? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemCoinListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    fun setOnItemClickListener(listener: (CoinItem) -> Unit) {
        onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coin = coinList[position]
        coin.let {
            holder.apply {
                bind(coin)
                itemView.tag = coin
                val changingRateTextView: TextView = holder.itemView.findViewById(R.id.changingRate)
                val arrowImageView: ImageView = holder.itemView.findViewById(R.id.changingDirection)
                val changingRateString:Double = Math.round(coin.marketCapChangePercentage24h * 10.0) / 10.0
                changingRateTextView.text = "%" + changingRateString.toString()
                if (coin.marketCapChangePercentage24h < 0) {
                    changingRateTextView.setTextColor(Color.RED)
                    arrowImageView.setImageResource(R.drawable.ic_arrow_down)
                } else if (coin.marketCapChangePercentage24h > 0) {
                    changingRateTextView.setTextColor(Color.GREEN)
                    arrowImageView.setImageResource(R.drawable.ic_arrow_up)
                } else {
                    changingRateTextView.setTextColor(Color.WHITE)
                    arrowImageView.visibility = View.GONE
                }

                itemView.setOnClickListener {
                    onItemClickListener?.let {
                        it(coin)
                        val pref = holder.itemView.context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
                        pref?.putString("coinId", coin.id)
                        pref?.putString("coinSymbol", coin.symbol)
                        pref?.putString("coinImage", coin.image)
                        pref?.putString("coinPrice", coin.currentPrice.toString())
                        pref?.apply()
                    }
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int { return coinList.size }

    fun submitList(coinLists: List<CoinItem>){
        coinList = coinLists
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemCoinListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CoinItem) {
            binding.apply {
                coins = item
            }
        }
    }

    fun addData(listItems: ArrayList<CoinItem>) {
        val size = listItems.size
        listItems.addAll(listItems)
        val sizeNew = listItems.size
        notifyItemRangeChanged(size, sizeNew)
    }
}