package com.swenson.android.task.network

import com.swenson.android.task.data.response.RepoResponseItem
import com.swenson.android.task.data.response.SearchCityResoponse
import com.swenson.android.task.data.response.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {


    @GET(Constants.WEATHER_FORECAST_URL)
    suspend fun fetchWeatherBasedCity(
        @Query("q") city : String ?= "Dubai",
        @Query("days") days : Int ?= 3,
        @Query("aqi") aqi : String ?= "no",
        @Query("alerts") alerts : String ?= "no",
    ) : WeatherResponse


    @GET(Constants.CITY_SEARCH_URL)
    suspend fun searchForCity(
        @Query("q") city : String ?= "Dubai",
    ) : SearchCityResoponse




}