package com.example.flightsearchapp.data.local.database.favorite

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDAO {
    @Query("SELECT * FROM favorite")
    fun getAllFavorite(): Flow<List<Favorite>>

    @Query("SELECT * FROM favorite WHERE departure_code LIKE :departureCode")
    fun getFavoriteByDepartureCode(departureCode: String): Flow<List<Favorite>>

    @Query(
        """
            SELECT COUNT(*)
            FROM favorite
            WHERE departure_code = :departureCode
            AND destination_code = :destinationCode
        """
    )
    suspend fun searchFavoriteFlightByDestinationAndDeparture(departureCode: String, destinationCode: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: Favorite)

    @Query(
        """
            DELETE FROM favorite
            WHERE departure_code = :departureCode
            AND destination_code = :destinationCode
        """
    )
    suspend fun deleteFavorite(departureCode: String, destinationCode: String)
}