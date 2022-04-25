package com.camo.template.database.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.camo.template.database.local.dao.*
import com.camo.template.database.remote.model.Ride
import com.camo.template.database.remote.model.User


// Increase version every time you make changes to room database structure
@Database(entities = [User::class, Ride::class], version = 2)
@TypeConverters(Converters::class)
abstract class LocalAppDb : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun rideDao(): RideDao
}