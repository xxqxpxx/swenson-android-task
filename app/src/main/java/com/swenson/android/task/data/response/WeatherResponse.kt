package com.swenson.android.task.data.response

data class WeatherResponse(
    val current: Current?= Current(),
    val forecast: ForecastDayList?= ForecastDayList(),
)

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

)

data class Condition(
    val text : String ?= "",
    val icon : String ?= ""
)

data class ForecastDayList(
    val forecastday: List<ForeCastDay>?= arrayListOf()
)

data class ForeCastDay(
    val date : String ?= "",
    val day: Current?= Current(),
)

