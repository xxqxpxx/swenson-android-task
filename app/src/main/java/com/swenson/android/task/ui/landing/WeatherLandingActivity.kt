package com.swenson.android.task.ui.landing

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.swenson.android.task.base.BaseActivity
import com.swenson.android.task.data.response.SearchCityResoponse
import com.swenson.android.task.data.response.SearchCityResoponseItem
import com.swenson.android.task.data.response.WeatherResponse
 import com.swenson.android.task.databinding.ActivityWeatherLandingBinding
import com.swenson.android.task.network.ResultModel
import dagger.hilt.android.AndroidEntryPoint
 import kotlinx.coroutines.launch
import java.util.*


@AndroidEntryPoint
class WeatherLandingActivity : BaseActivity<ActivityWeatherLandingBinding>() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2


    private var tempType = "c"
    private var tempC = 0.0
    private var tempF = 0.0

    private val viewModel: WeatherLandingViewModel by viewModels()

    private lateinit var foreCastAdapter : ForCastAdapter

    private lateinit var searchItemsAdapter:SearchItemsAdapter

    override fun getViewBinding() = ActivityWeatherLandingBinding.inflate(layoutInflater)


    var cityOnClickListener = (SearchItemsAdapter.OnClickListener { item ->
        handleCitySelection(item)
    })


    override fun setupView() {
        initListeners()
        initFusedLocationClient()
        initForeCastList()
    }


    @SuppressLint("SetTextI18n")
    private fun initListeners()
    {
        binding.txtTemp.setOnClickListener {
            if(tempType == "c")
            {
                binding.txtTemp.text = "$tempF °F"
                tempType = "f"
            }
            else if(tempType == "f")
            {
                binding.txtTemp.text = "$tempC °C"
                tempType = "c"
            }
        }


        binding.layoutError.button.setOnClickListener {
            viewModel.refresh(viewModel.localCity)
            hideErrorAndRefresh()
        }

        binding.icSearch.setOnClickListener{
            handleSearchLayout(true)
        }

    }

    private fun handleSearchLayout(showLayout: Boolean) {
        if (showLayout){
            binding.searchLayout.searchLayout.visibility = View.VISIBLE

            binding.searchLayout.imageView2.setOnClickListener {
                handleSearchLayout(false)
            }

            binding.searchLayout.imgClose.setOnClickListener {
                handleSearchLayout(false)
            }

            binding.searchLayout.bottomLayoutClose.setOnClickListener{
                handleSearchLayout(false)
            }

            binding.searchLayout.etSearch.addTextChangedListener {

                if (! it.isNullOrEmpty()){
                    if (it.length >= 2){
                        viewModel.searchForCity(it.toString().trim())
                    }
                }

            }

        }else{
            binding.searchLayout.searchLayout.visibility = View.GONE

        }
    }


    private fun hideErrorAndRefresh() {
        handleError(isError = false)
    }


    private fun handleError(isError: Boolean) {
        if (isError) {
            binding.layoutError.layoutError.visibility = View.VISIBLE
        } else {
            binding.layoutError.layoutError.visibility = View.GONE
        }
    }



    private fun initForeCastList()
    {
        foreCastAdapter = ForCastAdapter(context = this)
        binding.recyclerViewWeeklyWeather.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.recyclerViewWeeklyWeather.adapter = foreCastAdapter
    }


    private fun initCitiesList(){
        searchItemsAdapter = SearchItemsAdapter(context = this , onClickListener = cityOnClickListener)
        binding.searchLayout.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.searchLayout.recyclerView.adapter = searchItemsAdapter
    }


    override fun setupViewModelObservers() {
        viewModel.weatherDataObserver.observe(this,weatherDataObserver)
        viewModel.searchTextDataObserver.observe(this,  searchDataObserver)
    }

    private fun initFusedLocationClient()
    {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                        binding.txtAddress.text = list[0].adminArea

                        Log.i(TAG,"City : ${list[0].adminArea}")
                        viewModel.fetchPicturesData(city = list[0].adminArea)
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }


    private val weatherDataObserver = Observer<ResultModel<WeatherResponse>> { result ->
        lifecycleScope.launch {
            when (result) {
                is ResultModel.Loading -> {
                    handleProgress(isLoading = result.isLoading ?: false)
                }
                is ResultModel.Success -> {
                    onSuccess(data = result.data)
                }
                is ResultModel.Failure -> {
                    onFail()
                }
            }
        }
    }




    private fun handleCitySelection(item: SearchCityResoponseItem) {
        viewModel.fetchPicturesData(item.name)
        handleSearchLayout(false)

    }


    private val searchDataObserver = Observer<ResultModel<SearchCityResoponse>> { result ->
        lifecycleScope.launch {
            when (result) {
                is ResultModel.Loading -> {
                    handleProgress(isLoading = result.isLoading ?: false)
                }
                is ResultModel.Success -> {
                    onSuccess(data = result.data)
                }
                is ResultModel.Failure -> {
                    onFail()
                }
            }
        }
    }

    private fun handleProgress(isLoading : Boolean)
    {
        if(isLoading)
            binding.progressBar.visibility = View.VISIBLE
        else
            binding.progressBar.visibility = View.GONE

    }

    @SuppressLint("SetTextI18n")
    private fun onSuccess(data : SearchCityResoponse)
    {
        showCitiesRecycler()
        initCitiesList()

        searchItemsAdapter.submitList(data)
    }

    private fun showCitiesRecycler() {
        binding.searchLayout.recyclerView.visibility = View.VISIBLE
        binding.searchLayout.bottomLayoutClose.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun onSuccess(data : WeatherResponse)
    {
        handleProgress(isLoading = false)


        binding.txtAddress.text = viewModel.localCity

        tempC = data.current?.temp_c ?: 0.0
        tempF = data.current?.temp_f ?: 0.0

        binding.txtTemp.text = "$tempC°C"
        binding.txtCondition.text = "${data.current?.condition?.text}"

        binding.txtCondition.text = "${data.current?.condition?.text}"


        Glide.with(this)
            .applyDefaultRequestOptions(RequestOptions().centerCrop())
            .load("https:${data.current?.condition?.icon}")
            .into(binding.imgCondition)

        binding.txtDate.text = "${data.current?.last_updated}"


        binding.txtHumidity.text = "${data.current?.humidity} %"
        binding.txtWindSpeed.text = "${data.current?.wind_kph} kph"
    //    binding.txtWindDegree.text = "Wind Degree : ${data.current?.wind_degree} °"

        foreCastAdapter.submitList(data.forecast?.forecastday ?: arrayListOf())
    }

    private fun onFail()
    {
        handleProgress(isLoading = false)
        handleError(isError = true)

    }



}
