package com.cit.mycomposeapplication.models

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val timestamp: Long
)