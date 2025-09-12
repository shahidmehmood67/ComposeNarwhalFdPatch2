package com.cit.mycomposeapplication.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import com.cit.mycomposeapplication.models.LocationData
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationRepository(private val context: Context) {

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getLocationFlow(): Flow<String> = callbackFlow {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
//        PRIORITY_BALANCED_POWER_ACCURACY
            .setMinUpdateIntervalMillis(5000)
            .setMinUpdateDistanceMeters(50f) // Update only if moved 50 meters
//            .setWaitForAccurateLocation(true)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val lat = location.latitude
                    val lng = location.longitude

                    // Fetch location name in a coroutine
                    launch {
                        val locationName = getLocationName(context, lat, lng)
                        trySend(locationName)
                    }
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        awaitClose {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun formatAddress(address: Address?): String {
        if (address == null) return "Unknown location"

        val addressLines = mutableListOf<String>()

        // Add as many address lines as available
        for (i in 0..address.maxAddressLineIndex) {
            address.getAddressLine(i)?.let { addressLines.add(it) }
        }

        if (addressLines.isNotEmpty()) {
            return addressLines.joinToString(", ")
        }

        // Fallback if no address lines available
        val parts = mutableListOf<String>()
        if (!address.thoroughfare.isNullOrEmpty()) parts.add(address.thoroughfare)
        if (!address.locality.isNullOrEmpty()) parts.add(address.locality)
        if (!address.adminArea.isNullOrEmpty()) parts.add(address.adminArea)
        if (!address.countryName.isNullOrEmpty()) parts.add(address.countryName)

        return if (parts.isNotEmpty()) parts.joinToString(", ") else "Unknown location"
    }

    suspend fun getLocationName(context: Context, latitude: Double, longitude: Double): String {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val address = geocoder.getFromLocation(latitude, longitude, 1)
                val firstAddress = address?.firstOrNull()
                val locationName = when {
                    !firstAddress?.locality.isNullOrEmpty() -> firstAddress?.locality
                    !firstAddress?.subAdminArea.isNullOrEmpty() -> firstAddress?.subAdminArea
                    !firstAddress?.adminArea.isNullOrEmpty() -> firstAddress?.adminArea
                    !firstAddress?.countryName.isNullOrEmpty() -> firstAddress?.countryName
                    else -> null
                }
                locationName ?: "Unknown Location"
            } catch (e: Exception) {
                "Unknown Location"
            }
        }
    }

    suspend fun fetchUserLocation(): Location? {
        return withContext(Dispatchers.IO) {
            try {
                fusedLocationProviderClient.lastLocation.await()
            } catch (e: Exception) {
                null
            }
        }
    }


    private suspend fun geocodeLocation(location: Location): LocationData = withContext(Dispatchers.IO) {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addressText: String? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // For Android 13+ use the new API
//                geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
//                    addressText = formatAddress(addresses.firstOrNull())
//                }
                addressText = suspendCoroutine { continuation ->
                    geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                        val formattedAddress = formatAddress(addresses.firstOrNull())
                        continuation.resume(formattedAddress)
                    }
                }

            } else {
                // For older Android versions
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                addressText = formatAddress(addresses?.firstOrNull())
            }
        } catch (e: IOException) {
            Log.e("TAG", "Geocoder error", e)
            addressText = "Unable to determine address"
        } catch (e: Exception) {
            Log.e("TAG", "General geocoding error", e)
            addressText = "Error getting location name"
        }

        return@withContext LocationData(
            latitude = location.latitude,
            longitude = location.longitude,
            address = addressText,
            timestamp = System.currentTimeMillis()
        )
    }


}

