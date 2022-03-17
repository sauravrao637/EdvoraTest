package com.camo.template.database

import com.camo.template.database.local.LocalAppDb
import com.camo.template.database.remote.api.ETApiHelper
import com.camo.template.database.remote.model.Rides
import com.camo.template.database.remote.model.User
import com.camo.template.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Repository @Inject constructor(private val db: LocalAppDb, val cgApiHelper: ETApiHelper) {
//    suspend fun pingCG(): Flow<Resource<Response<Any>>> {
//        return flow {
//            emit(Resource.loading(data = null))
//            try {
//                val res = cgApiHelper.ping()
//                Timber.d(res.toString())
//                if (res.isSuccessful && res.code() == 200) emit(Resource.success(res))
//                else {
//                    Timber.d(res.toString())
//                    emit(Resource.error(res, "Couldn't ping server"))
//                }
//            } catch (e: Exception) {
//                Timber.d(e)
//                emit(Resource.error(null, "Couldn't ping server"))
//            }
//        }
//    }
//
//    suspend fun addCoins(coins: ArrayList<Coin>) {
//        db.coinDao().addCoins(coins)
//    }

    suspend fun getRidesFlow(): Flow<Resource<Rides>>{
        return flow {
            emit(Resource.loading())
            emit(cgApiHelper.getRides())
        }
    }

    fun getUserFlow(): Flow<Resource<User>> {
        return flow{
            emit(Resource.loading())
            emit(cgApiHelper.getUser())
        }
    }
}