package com.example.geofenceapidemokotlin

import android.Manifest
import android.R.attr
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Color.argb
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    private lateinit var map: GoogleMap

    private lateinit var geofencingClient: GeofencingClient

    private lateinit var geofenceHelper: GeofenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        geofenceHelper = GeofenceHelper(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        map.isMyLocationEnabled = true


        map.setOnMapLongClickListener { latlng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latlng))
            addCircularRegion(latlng, radius.toDouble())
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val geofence = geofenceHelper.getGeofence(
                    GEOFENCE_ID,
                    latlng,
                    radius,
                    Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT
                )
                val geofencingRequest: GeofencingRequest =
                    geofenceHelper.geoFencingRequest(geofence)
                val pendingIntent = geofenceHelper.getPendingIntent()
                geofencingClient.addGeofences(geofencingRequest, pendingIntent!!)
                    .addOnSuccessListener {
                        Log.d(TAG, "onSuccess: Geofence Added ..")
                    }
                    .addOnFailureListener {
                        val errorMessage = geofenceHelper.getErrorString(it)
                        Log.d(TAG, "onFailure: $errorMessage")
                    }
            }

        }
    }

    fun addCircularRegion(latLng: LatLng, radius: Double) {
        val circleOptions = CircleOptions().center(latLng)
            .radius(radius)
            .strokeColor(argb(255, 255, 0, 0))
            .fillColor(argb(64, 255, 0, 0))
        map.addCircle(circleOptions)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

    }

    companion object {
        private const val GEOFENCE_ID = "geofence_id"
        private const val TAG = "MapsActivity"
        private const val radius = 200f
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    override fun onMyLocationClick(location: Location) {
        val currentLocation = LatLng(location.latitude, location.longitude)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 18f))
    }
}