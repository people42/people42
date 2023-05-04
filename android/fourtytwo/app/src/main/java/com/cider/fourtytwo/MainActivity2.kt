package com.cider.fourtytwo

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cider.fourtytwo.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var myLocationListener: MyLocationListener
    private lateinit var binding: ActivityMain2Binding

    companion object {
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val responsePermissions = permissions.entries.filter {
                it.key in locationPermissions
            }

            if (responsePermissions.filter { it.value == true }.size == locationPermissions.size) {
                setLocationListener()
            } else {
                Toast.makeText(this, "no", Toast.LENGTH_SHORT).show()
            }
        }

    private fun getMylocation() {
        if (::locationManager.isInitialized.not()) {
            locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        val isGpsEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGpsEnable) {
            permissionLauncher.launch(locationPermissions)
        }
    }

    @Suppress("MissingPermission")
    private fun setLocationListener() {
        val minTime: Long = 1500
        val minDistance = 100f

        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = MyLocationListener()
        }

        with(locationManager) {
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime, minDistance, myLocationListener
            )

            requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime, minDistance, myLocationListener
            )
        }
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            Toast
                .makeText(this@MainActivity2, "${location.latitude}, ${location.longitude}", Toast.LENGTH_SHORT)
                .show()

            binding.locationTitleTextView.text = "${location.latitude}, ${location.longitude}"

            removeLocationListener()
        }

        private fun removeLocationListener() {
            if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
                locationManager.removeUpdates(myLocationListener)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater); // 1
        setContentView(binding.root)
        getMylocation()
    }
}

//        val manager = getSystemService(LOCATION_SERVICE) as LocationManager
//        var result = "All Providers"
//        val providers = manager.allProviders
//        for (provider in providers){
//            result += "$provider, "
//            Log.d(TAG, "onCreate: $result")
//        }
//        result = "Enabled Providers: "
//        val EnabledProviders = manager.getProviders(true)
//        for (provider in EnabledProviders){
//            result += "$provider, "
//            Log.d(TAG, "onCreate: $result")
//        }
//
//        val listener: LocationListener = object : LocationListener{
//            override fun onLocationChanged(location: Location) {
//                Log.d(TAG, "onLocationChanged: ${location.latitude}, ${location.longitude}")
//            }
//            override fun onProviderDisabled(provider: String) {
//                super.onProviderDisabled(provider)
//            }
//            override fun onProviderEnabled(provider: String) {
//                super.onProviderEnabled(provider)
//            }
//        }
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10_000L, 10f, listener)
//        manager.removeUpdates(listener)
//    }
//}