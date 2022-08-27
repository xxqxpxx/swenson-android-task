package com.swenson.android.task.ui.landing

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.swenson.android.task.base.BaseViewModel
import com.swenson.android.task.data.repo.ReposRepository
import com.swenson.android.task.data.response.WeatherResponse
import com.swenson.android.task.network.ResultModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WeatherLandingViewModel @Inject constructor(private val repository: ReposRepository) :
    BaseViewModel() {


    private val _reposDataObserver = MutableLiveData<ResultModel<List<WeatherResponse>>>()
    val reposDataObserver: LiveData<ResultModel<List<WeatherResponse>>> = _reposDataObserver


    var list: List<WeatherResponse> = arrayListOf()



    private val TAG = "WeatherViewModel"

    private val _weatherDataObserver = MutableLiveData<ResultModel<WeatherResponse>>()
    val weatherDataObserver: LiveData<ResultModel<WeatherResponse>> = _weatherDataObserver


     fun fetchPicturesData(city : String)
    {


        _weatherDataObserver.postValue(ResultModel.Loading(isLoading = true))
        viewModelScope.launch {
            repository.fetchWeatherBasedCity(city = "Dubai")
                .catch { exception ->
                    Log.i(TAG,"Exception : ${exception.message}")
                    _weatherDataObserver.value = ResultModel.Failure(code = getStatusCode(throwable = exception))
                    _weatherDataObserver.postValue(ResultModel.Loading(isLoading = false))
                } // exception
                .collect { response ->
                    Log.i(TAG,"Response : $response")
                    _weatherDataObserver.postValue(ResultModel.Success(data = response))
                } // collect
        }
    } // fun of fetchTeamMainData

    fun refresh(city : String){
        fetchPicturesData(city)
    }

}