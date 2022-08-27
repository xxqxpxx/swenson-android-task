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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.swenson.android.task.base.BaseActivity
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


    override fun getViewBinding() = ActivityWeatherLandingBinding.inflate(layoutInflater)

    override fun setupView() {
        initListeners()
        initFusedLocationClient()
        initForeCastList()
    } // fun of setupView


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
    } // fun of initListeners

    private fun initForeCastList()
    {
        foreCastAdapter = ForCastAdapter(context = this)
        binding.recyclerViewWeeklyWeather.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.recyclerViewWeeklyWeather.adapter = foreCastAdapter
    }// fun of initForeCastList

    override fun setupViewModelObservers() {
        viewModel.weatherDataObserver.observe(this,weatherDataObserver)
    } // fun of setupViewModelObservers

    private fun initFusedLocationClient()
    {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()
    } // fun of initFusedLocationClient

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    } // fun of isLocationEnabled

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
    } // fun of checkPermissions
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    } // fun of requestPermissions
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
    } // fun of onRequestPermissionsResult

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)

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
    } // fun of getLocation


    private val weatherDataObserver = Observer<ResultModel<WeatherResponse>> { result ->
        lifecycleScope.launch {
            when (result) {
                is ResultModel.Loading -> {
                    handleProgress(isLoading = result.isLoading ?: false)
                } // Loading
                is ResultModel.Success -> {
                    onSuccess(data = result.data)
                } // Success
                is ResultModel.Failure -> {
                    onFail()
                } // Fail
            }
        }
    } // weatherDataObserver

    private fun handleProgress(isLoading : Boolean)
    {
        if(isLoading)
            binding.progressBar.visibility = View.VISIBLE
        else
            binding.progressBar.visibility = View.GONE

    } // fun of handleProgress

    @SuppressLint("SetTextI18n")
    private fun onSuccess(data : WeatherResponse)
    {
        handleProgress(isLoading = false)

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
    } // fun of onSuccess

    private fun onFail()
    {
        handleProgress(isLoading = false)

    } // fun of onFail

}
