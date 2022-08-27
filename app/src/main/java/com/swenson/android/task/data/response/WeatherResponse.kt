package com.swenson.android.task.data.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WeatherResponse(
    val current: Current?= Current(),
    val forecast: ForecastDayList?= ForecastDayList(),
)  : Parcelable

@Parcelize
data class Current(
    val temp_c : Double ?= 0.0,
    val temp_f : Double ?= 0.0,
    val condition: Condition?= Condition(),
    val humidity  : Double ?= 0.0,
    val wind_kph  : Double ?= 0.0,
    val wind_degree  : Double ?= 0.0,
    val maxtemp_c   : Double ?= 0.0,
    val mintemp_c   : Double ?= 0.0,
    val last_updated :String? = "",

)  : Parcelable

@Parcelize
data class Condition(
    val text : String ?= "",
    val icon : String ?= ""
)  : Parcelable

@Parcelize
data class ForecastDayList(
    val forecastday: List<ForeCastDay>?= arrayListOf()
) : Parcelable

@Parcelize
data class ForeCastDay(
    val date : String ?= "",
    val day: Current?= Current(),
) : Parcelable

