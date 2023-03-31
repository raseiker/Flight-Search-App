package com.example.flightsearchapp.ui.homeScreen

import com.example.flightsearchapp.data.local.database.airport.Airport

/**
 * Sealed interface for User [HomeScreen] interaction
 */
sealed interface HomeScreenEvent {
    data class InputSearch(val input: String): HomeScreenEvent
    data class ToFavoriteAirport(val destination: Airport, val departure: Airport, val isFavorite: Boolean): HomeScreenEvent
    data class OnClickSuggestion(val departure: Airport): HomeScreenEvent
    data class ChangeAppColor(val darkMode: Boolean): HomeScreenEvent
}

/**
 * Data class for hold [FlightState]
 */
data class FlightState(
    val flightUI: List<FlightUI> = emptyList(),
    val isVisible: Boolean = false
)

data class FlightUI(
    val destination: Airport = Airport(),
    val isFavorite: Boolean = false
)

/**
 * Data class for hold [SuggestionState]
 */

data class SuggestionState(
    val suggestionList: List<Airport> = emptyList(),
    val isVisible: Boolean = false
)

/**
 * Data class for hold [FavoriteState]
 */
data class FavoriteState(
    val favoriteList: List<FavoriteUI> = emptyList(),
    val isVisible: Boolean = true
)

data class FavoriteUI(
    val destination: Airport = Airport(),
    val departure: Airport = Airport(),
    val isFavorite: Boolean = true
)