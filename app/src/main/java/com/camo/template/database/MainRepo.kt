package com.camo.template.database

import androidx.room.withTransaction
import com.camo.template.database.local.LocalAppDb
import com.camo.template.database.remote.api.ETService
import com.camo.template.util.networkBoundResource
import javax.inject.Inject

class Repository @Inject constructor(private val db: LocalAppDb, val api: ETService) {
    private val userDao = db.userDao()
    private val rideDao = db.rideDao()

    fun getRides() = networkBoundResource(
        query = {
            rideDao.getAllRides()
        },
        fetch = {
            api.getRides()
        },
        saveFetchResult = { rides ->
            db.withTransaction {
                rideDao.deleteAllRides()
                rideDao.insertRides(rides)
            }
        }
    )

    fun getUser() = networkBoundResource(
        query = {
            userDao.getUser()
        },
        fetch = {
            api.getUser()
        },
        saveFetchResult = {
            user ->
            db.withTransaction {
                userDao.deleteUsers()
                userDao.addUser(user)
            }
        }
    )
}