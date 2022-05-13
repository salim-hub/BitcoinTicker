package com.salim.bitcointicker.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.salim.bitcointicker.R
import com.salim.bitcointicker.databinding.ItemCoinDetailsBinding
import com.salim.bitcointicker.network.dto.details.Details
import javax.inject.Inject

class DetailRecyclerAdapter @Inject constructor(
    val glide: RequestManager
) : RecyclerView.Adapter<DetailRecyclerAdapter.ViewHolder>() {

    private lateinit var recyclerView: RecyclerView
    var coinDetails: List<Details> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCoinDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coinDetail = coinDetails[position]
        coinDetail.let {
            holder.apply {
                bind(coinDetail)
                itemView.tag = coinDetail
                val changingRateTextView: TextView = holder.itemView.findViewById(R.id.priceChangePercentage24hTV)
                val arrowImageView: ImageView = holder.itemView.findViewById(R.id.changingDirection)
                val changingRateString:Double = Math.round(coinDetail.marketData.marketCapChangePercentage24h * 10.0) / 10.0
                changingRateTextView.text = "%" + changingRateString.toString()
                if (coinDetail.marketData.marketCapChangePercentage24h < 0) {
                    changingRateTextView.setTextColor(Color.RED)
                    arrowImageView.setImageResource(R.drawable.ic_arrow_down)
                } else if (coinDetail.marketData.marketCapChangePercentage24h > 0) {
                    changingRateTextView.setTextColor(Color.GREEN)
                    arrowImageView.setImageResource(R.drawable.ic_arrow_up)
                } else {
                    changingRateTextView.setTextColor(Color.WHITE)
                    arrowImageView.visibility = View.GONE
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int { return coinDetails.size }

    fun submitList(coinDetailList: List<Details>) {
        coinDetails = coinDetailList
        notifyDataSetChanged()
    }
    
    class ViewHolder(private val binding: ItemCoinDetailsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Details) {
            binding.apply {
                details = item
            }
        }
    }

    fun addData(listItems: ArrayList<Details>) {
        val size = listItems.size
        listItems.addAll(listItems)
        val sizeNew = listItems.size
        notifyItemRangeChanged(size, sizeNew)
    }
}