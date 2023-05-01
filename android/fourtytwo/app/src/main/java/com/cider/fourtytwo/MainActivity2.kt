package com.cider.fourtytwo

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.TextSwitcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient


class MainActivity2 : AppCompatActivity() {
    private var stringIndex = 0
    private val row = arrayOf("ONE", "TWO", "THREE", "FORE", "FIVE")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val textSwitcher = findViewById<TextSwitcher>(R.id.custom_switcher)
        textSwitcher.setOnClickListener {
            textSwitcher.setText(row[stringIndex])
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


    }
}