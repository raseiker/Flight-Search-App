package com.example.flightsearchapp.data.local.database.airport

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDAO {
    @Query("""
        SELECT * FROM airport 
        WHERE name LIKE :name
        OR iata_code LIKE :iataCode
        """)
    fun getAirportByNameOrIataCode(name: String, iataCode: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code LIKE :iataCode")
    fun getAirportByIataCode(iataCode: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport ORDER BY name")
    fun getAllAirport(): Flow<List<Airport>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAirport(airport: Airport)
}