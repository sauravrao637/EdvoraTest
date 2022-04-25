package com.camo.template.database.remote.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user")
data class User(
    @SerializedName("station_code")
    val stationCode: Double,
    @SerializedName("name")
    @PrimaryKey
    val name: String,
    @SerializedName("url")
    val url: String
)