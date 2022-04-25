package com.camo.template.database.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.camo.template.database.remote.model.Ride
import com.camo.template.database.remote.model.Rides
import com.camo.template.database.remote.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User)

    @Query("DELETE FROM user")
    suspend fun deleteUsers()

    @Query("Select * FROM user Limit 1")
    fun getUser(): Flow<User>
}

@Dao
interface RideDao{
    @Query("SELECT * FROM rides")
    fun getAllRides(): Flow<List<Ride>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRides(rides: List<Ride>)

    @Query("DELETE FROM rides")
    suspend fun deleteAllRides()
}