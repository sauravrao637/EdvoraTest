package com.camo.template.database.remote.model


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("station_code")
    val stationCode: Double,
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)