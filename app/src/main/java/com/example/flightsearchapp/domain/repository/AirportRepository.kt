package com.example.flightsearchapp.domain.repository

import com.example.flightsearchapp.data.local.database.airport.Airport
import kotlinx.coroutines.flow.Flow

interface AirportRepository {
    fun getAirportByNameOrIataCode(name: String, iataCode: String): Flow<List<Airport>>
    fun getAirportByIataCode(iataCode: String): Flow<List<Airport>>
    fun getAllAirport(): Flow<List<Airport>>
    suspend fun insertAirport(airport: Airport)
}