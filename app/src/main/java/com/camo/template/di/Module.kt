package com.camo.template.di

import android.content.Context
import androidx.room.Room
import com.camo.template.database.Repository
import com.camo.template.database.local.LocalAppDb
import com.camo.template.database.remote.api.ETService
import com.camo.template.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Singleton
    @Provides
    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build() //Doesn't require the adapter
    }

    @Provides
    @Singleton
    fun getETService(): ETService = getRetrofit().create(ETService::class.java)

    @Provides
    @Singleton
    fun getAppDb(@ApplicationContext context: Context): LocalAppDb = Room.databaseBuilder(
        context.applicationContext,
        LocalAppDb::class.java,
        "appDB.db"
    ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun getRepo(@ApplicationContext context: Context): Repository =
        Repository(getAppDb(context), getETService())

}