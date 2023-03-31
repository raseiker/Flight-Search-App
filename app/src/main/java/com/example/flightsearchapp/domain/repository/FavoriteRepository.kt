package com.example.flightsearchapp.domain.repository

import com.example.flightsearchapp.data.local.database.favorite.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavoriteByDepartureCode(departureCode: String): Flow<List<Favorite>>
    fun getAllFavorite(): Flow<List<Favorite>>
    suspend fun insertFavorite(favorite: Favorite)
    suspend fun deleteFavorite(favorite: Favorite)
    suspend fun searchFavoriteFlightByDepartureAndDestination(departureCode: String, destinationCode: String): Int
}