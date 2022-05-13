package com.salim.bitcointicker.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.salim.bitcointicker.R
import java.text.NumberFormat
import java.util.*

@BindingAdapter("imageFromUrl")
fun loadImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        val options = RequestOptions()
            .error(R.drawable.ic_bitcoin)
        Glide.with(view.context)
            .setDefaultRequestOptions(options)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

@BindingAdapter("marketCap")
fun convertString(view: TextView, number: Long?) {
    val format = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 0
    format.currency = Currency.getInstance("USD")
    val formattedNumberMarketCap = format.format(number)
    view.text = formattedNumberMarketCap
}

@BindingAdapter("price")
fun convertPriceToString(view: TextView, number: Double) {
    val format = NumberFormat.getCurrencyInstance()
    format.currency = Currency.getInstance("USD")
    val formattedNumberPrice = format.format(number)
    view.text = formattedNumberPrice
}
