package com.example.flightsearchapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.flightsearchapp.data.local.database.airport.Airport
import com.example.flightsearchapp.data.local.database.airport.AirportDAO
import com.example.flightsearchapp.data.local.database.favorite.Favorite
import com.example.flightsearchapp.data.local.database.favorite.FavoriteDAO

@Database(entities = [Airport::class, Favorite::class], version = 1, exportSchema = false)
abstract class FlightDatabase: RoomDatabase() {

    abstract val airportDAO : AirportDAO
    abstract val favoriteDAO : FavoriteDAO
}