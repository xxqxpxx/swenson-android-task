package com.swenson.android.task.data.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class SearchCityResoponse : ArrayList<SearchCityResoponseItem>()

@Parcelize
data class SearchCityResoponseItem(
    val country: String,
    val id: Int,
    val lat: Double,
    val lon: Double,
    val name: String,
    val region: String,
    val url: String
) : Parcelable