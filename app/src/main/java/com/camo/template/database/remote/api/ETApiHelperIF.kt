package com.camo.template.database.remote.api

import com.camo.template.database.remote.model.Rides
import com.camo.template.database.remote.model.User
import com.camo.template.util.Resource
import retrofit2.Response

interface ETApiHelperIF {
//    suspend fun ping(): Response<Any>
    suspend fun getRides(): Resource<Rides>
    suspend fun getUser(): Resource<User>
}
