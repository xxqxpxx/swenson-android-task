package com.swenson.android.task.ui.landing

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.swenson.android.task.data.response.ForeCastDay
import com.swenson.android.task.databinding.ItemWeatherBinding

class ForCastAdapter(private var forecastList : List<ForeCastDay> ?= arrayListOf(), private val context: Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        @SuppressLint("NotifyDataSetChanged")
        fun submitList(playerList : List<ForeCastDay>)
        {
            this.forecastList  = playerList
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return forecastList?.size ?: 0
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return ViewHolderForecast(
                ItemWeatherBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            forecastList?.get(position)
                ?.let { (holder as ViewHolderForecast).bind(context = context,forecast = it) }
        }

        private class ViewHolderForecast    (val binding: ItemWeatherBinding) : RecyclerView.ViewHolder(binding.root) {
            @SuppressLint("SetTextI18n")
            fun bind(context: Context, forecast : ForeCastDay)
            {
                binding.txtDay.text = "${forecast.date}"
                binding.txtTemp.text = "${forecast.day?.maxtemp_c} °C / ${forecast.day?.mintemp_c} °C"

                Glide.with(context)
                    .applyDefaultRequestOptions(RequestOptions().centerCrop())
                    .load("https:${forecast.day?.condition?.icon}")
                    .into(binding.imgCondition)
            }
        }
    }