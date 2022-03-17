package com.camo.template.database.remote.api

import com.camo.template.database.remote.model.Rides
import com.camo.template.database.remote.model.User
import com.camo.template.util.Resource
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

const val slowCalls = true

class ETApiHelper @Inject constructor(private val etService: ETService) : ETApiHelperIF {
    //    override suspend fun ping() = etService.ping()
    override suspend fun getRides(): Resource<Rides> {
        if (slowCalls) delay(1000)
        return try {
            val res = etService.getRides()
            if (res.isSuccessful) {
                Resource.success(res.body() as Rides)
            } else {
                Resource.error(data = null, errorInfo = "Could Not Get Data")
            }
        } catch (e: Exception) {
            Resource.error(null, errorInfo = e.localizedMessage ?: "Error")
        }
    }

    override suspend fun getUser(): Resource<User> {
        if (slowCalls) delay(1000)
        return try {
            val res = etService.getUser()
            Timber.d("${res.body()}")
            if (res.isSuccessful) {
                val user = res.body() as User
                Resource.success(user)
            } else {
                Resource.error(data = null, errorInfo = "Could Not Get Data")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.error(null, errorInfo = e.localizedMessage ?: "Error")
        }
    }
}