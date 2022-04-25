package com.camo.template.database.remote.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

typealias Rides = ArrayList<Ride>
@Entity(tableName = "rides")
data class Ride(
    @SerializedName("city")
    val city: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("destination_station_code")
    val destinationStationCode: Int,
    @SerializedName("id")
    @PrimaryKey
    val id: Int,
    @SerializedName("map_url")
    val mapUrl: String,
    @SerializedName("origin_station_code")
    val originStationCode: Int,
    @SerializedName("state")
    val state: String,
    @SerializedName("station_path")
    val stationPath: List<Int>,
    var dist: Int?
){
    val upcoming: Boolean get() = run {
        val dt = Date(date)
        Timber.d("${dt.time}, ${System.currentTimeMillis()}")
        return System.currentTimeMillis() <= dt.time
    }
}
