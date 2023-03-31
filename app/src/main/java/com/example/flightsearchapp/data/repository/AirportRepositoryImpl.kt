package com.example.flightsearchapp.data.repository

import com.example.flightsearchapp.data.local.database.airport.Airport
import com.example.flightsearchapp.data.local.database.airport.AirportDAO
import com.example.flightsearchapp.domain.repository.AirportRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AirportRepositoryImpl @Inject constructor(
    private val airportDAO: AirportDAO
): AirportRepository {
    override fun getAirportByNameOrIataCode(name: String, iataCode: String): Flow<List<Airport>> = airportDAO.getAirportByNameOrIataCode(name, iataCode)

    override fun getAirportByIataCode(iataCode: String): Flow<List<Airport>> = airportDAO.getAirportByIataCode(iataCode)

    override fun getAllAirport(): Flow<List<Airport>> = airportDAO.getAllAirport()

    override suspend fun insertAirport(airport: Airport) {
        airportDAO.insertAirport(airport)
    }
}