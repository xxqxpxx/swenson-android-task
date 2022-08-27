package com.swenson.android.task.ui.landing

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.swenson.android.task.base.BaseViewModel
import com.swenson.android.task.data.repo.WeatherRepository
import com.swenson.android.task.data.response.SearchCityResoponse
import com.swenson.android.task.data.response.WeatherResponse
import com.swenson.android.task.network.ResultModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WeatherLandingViewModel @Inject constructor(private val repository: WeatherRepository) :
    BaseViewModel() {


    var list: List<WeatherResponse> = arrayListOf()

    private val TAG = "WeatherViewModel"

    private val _weatherDataObserver = MutableLiveData<ResultModel<WeatherResponse>>()
    val weatherDataObserver: LiveData<ResultModel<WeatherResponse>> = _weatherDataObserver

    private val _searchTextDataObserver = MutableLiveData<ResultModel<SearchCityResoponse>>()
    val searchTextDataObserver: LiveData<ResultModel<SearchCityResoponse>> = _searchTextDataObserver

    var localCity = ""
    fun fetchPicturesData(city: String) {

        localCity = city

        _weatherDataObserver.postValue(ResultModel.Loading(isLoading = true))
        viewModelScope.launch {
            repository.fetchWeatherBasedCity(city = city)
                .catch { exception ->
                    Log.i(TAG, "Exception : ${exception.message}")
                    _weatherDataObserver.value =
                        ResultModel.Failure(code = getStatusCode(throwable = exception))
                    _weatherDataObserver.postValue(ResultModel.Loading(isLoading = false))
                }
                .collect { response ->
                    Log.i(TAG, "Response : $response")
                    _weatherDataObserver.postValue(ResultModel.Success(data = response))
                }
        }
    }

    fun searchForCity(city: String) {
        localCity = city
        _searchTextDataObserver.postValue(ResultModel.Loading(isLoading = true))
        viewModelScope.launch {
            repository.searchForCity(city = city)
                .catch { exception ->
                    Log.i(TAG, "Exception : ${exception.message}")
                    _searchTextDataObserver.value =
                        ResultModel.Failure(code = getStatusCode(throwable = exception))
                    _searchTextDataObserver.postValue(ResultModel.Loading(isLoading = false))
                }
                .collect { response ->
                    Log.i(TAG, "Response : $response")
                    _searchTextDataObserver.postValue(ResultModel.Success(data = response))
                }
        }
    }


    fun refresh(city: String) {
        fetchPicturesData(city)
    }

}