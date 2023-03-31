package com.example.flightsearchapp.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.flightsearchapp.data.local.database.FlightDatabase
import com.example.flightsearchapp.data.repository.AirportRepositoryImpl
import com.example.flightsearchapp.data.repository.FavoriteRepositoryImpl
import com.example.flightsearchapp.domain.repository.AirportRepository
import com.example.flightsearchapp.domain.repository.FavoriteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //provide database
    @Singleton
    @Provides
    fun provideFlightDataBase(app: Application): FlightDatabase {
        return Room.databaseBuilder(
            app,
            FlightDatabase::class.java,
            "flightDatabase"
        )
            .fallbackToDestructiveMigration()
            .createFromAsset("FlightSearchDataBase/flight_search_db.db")
            .build()
    }

    //provide dao's

    //provide bind airport repo impl
    @Singleton
    @Provides
    fun provideAirportRepository(db: FlightDatabase): AirportRepository{
        return AirportRepositoryImpl(db.airportDAO)
    }

    //provide bind favorite repo impl
    @Singleton
    @Provides
    fun provideFavoriteRepository(db: FlightDatabase): FavoriteRepository {
        return FavoriteRepositoryImpl(db.favoriteDAO)
    }

    //provide dataStore via it factory
    @Singleton
    @Provides
    fun provideUserSearchPreferences(app: Application): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler (
                produceNewData = { emptyPreferences()}
            ),
            produceFile = {app.preferencesDataStoreFile("user_preferences")}
        )
    }


}