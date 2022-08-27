package com.swenson.android.task.data.repo

import android.util.Log
import com.swenson.android.task.data.response.RepoResponseItem
import com.swenson.android.task.data.response.SearchCityResoponse
import com.swenson.android.task.data.response.WeatherResponse
import com.swenson.android.task.network.ApiService
import com.swenson.android.task.network.Constants.NUMBER_OF_REPOS_PER_PAGE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val apiService: ApiService) {

    private val TAG = "Weather Repository"

/*

    fun fetchRepos(page: Int): Flow<retrofit2.Response<List<RepoResponseItem>>> {
        return flow {
            val response = apiService.getRepository(NUMBER_OF_REPOS_PER_PAGE, page)
            Log.i(TAG, "fetchMain response $response")
            emit(response)
        }
    }
*/


    fun fetchWeatherBasedCity(city : String) : Flow<WeatherResponse> {
        return flow {
            val response = apiService.fetchWeatherBasedCity(city = city)
            Log.i(TAG, "fetchMainAlbums response $response")
            emit(response)
        }
    }



    fun searchForCity(city : String) : Flow<SearchCityResoponse> {
        return flow {
            val response = apiService.searchForCity(city = city)
            Log.i(TAG, "fetchMainAlbums response $response")
            emit(response)
        }
    }



}