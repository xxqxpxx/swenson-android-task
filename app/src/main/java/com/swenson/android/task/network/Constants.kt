package com.swenson.android.task.network

import com.swenson.android.task.BuildConfig
import com.swenson.android.task.BuildConfig.API_KEY

object Constants {

    const val API_TIMEOUT: Long = 60

    const val BASE_URL = BuildConfig.GITHUB_BASE_URL

    const val WEATHER_FORECAST_URL =  "forecast.json?key=$API_KEY"

    const val CITY_SEARCH_URL =  "search.json?key=$API_KEY"


    const val PREFERENCE_NAME = "pref_swenson"

    const val REPOS_LIST_PREF_NAME = "repos_list"

    const val NUMBER_OF_REPOS_PER_PAGE = 15

    const val STARTING_KEY = 1

}