package com.example.geofenceapidemokotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        Toast.makeText(context, "Geofence Triggered", Toast.LENGTH_SHORT).show()

        val geofenceEvent = GeofencingEvent.fromIntent(intent)

        if (geofenceEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofencing event..")
            return
        }

        //val location = geofenceEvent.triggeringLocation
        val geofenceList = geofenceEvent.triggeringGeofences
        for (geofence in geofenceList) {
            Log.d(TAG, "Geofence ID: ${geofence.requestId}")
        }
        val transitionType = geofenceEvent.geofenceTransition
        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER ->
                Toast.makeText(context, "ENTER TRANSITION TYPE", Toast.LENGTH_SHORT).show()
            Geofence.GEOFENCE_TRANSITION_DWELL ->
                Toast.makeText(context, "DWELL TRANSITION TYPE", Toast.LENGTH_SHORT).show()
            Geofence.GEOFENCE_TRANSITION_EXIT ->
                Toast.makeText(context, "EXIT TRANSITION TYPE", Toast.LENGTH_SHORT).show()
        }


    }

    companion object {
        const val TAG = "GeofenceBroadcastRece"
    }
}