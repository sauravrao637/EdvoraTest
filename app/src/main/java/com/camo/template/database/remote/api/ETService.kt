package com.camo.template.database.remote.api

import com.camo.template.database.remote.model.Ride
import com.camo.template.database.remote.model.Rides
import com.camo.template.database.remote.model.User
import com.camo.template.util.Resource
import retrofit2.Response
import retrofit2.http.GET

interface ETService {
//    @GET("ping")
//    suspend fun ping(): Response<Any>
    @GET("rides")
    suspend fun getRides(): List<Ride>

    @GET("user")
    suspend fun getUser(): User
}