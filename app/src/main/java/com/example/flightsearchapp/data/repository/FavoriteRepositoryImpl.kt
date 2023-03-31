package com.example.flightsearchapp.data.repository

import com.example.flightsearchapp.data.local.database.favorite.Favorite
import com.example.flightsearchapp.data.local.database.favorite.FavoriteDAO
import com.example.flightsearchapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDAO: FavoriteDAO
): FavoriteRepository {
    override fun getFavoriteByDepartureCode(departureCode: String): Flow<List<Favorite>> = favoriteDAO.getFavoriteByDepartureCode(departureCode)

    override fun getAllFavorite(): Flow<List<Favorite>> = favoriteDAO.getAllFavorite()

    override suspend fun insertFavorite(favorite: Favorite) {
        favoriteDAO.insertFavorite(favorite)
    }

    override suspend fun deleteFavorite(favorite: Favorite) {
        favoriteDAO.deleteFavorite(favorite.departureCode, favorite.destinationCode)
    }

    override suspend fun searchFavoriteFlightByDepartureAndDestination(
        departureCode: String,
        destinationCode: String
    ) = favoriteDAO.searchFavoriteFlightByDestinationAndDeparture(departureCode, destinationCode)
}