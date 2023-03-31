package com.example.flightsearchapp.ui.homeScreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearchapp.data.local.database.airport.Airport
import com.example.flightsearchapp.data.local.database.favorite.Favorite
import com.example.flightsearchapp.data.local.preferences.UserSearchPreferencesRepository
import com.example.flightsearchapp.domain.repository.AirportRepository
import com.example.flightsearchapp.domain.repository.FavoriteRepository
import com.example.flightsearchapp.utils.ResponseUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * view model of [HomeScreen] components
 */
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val airportRepository: AirportRepository,
    private val favoriteRepository: FavoriteRepository,
    private val userSearchPreferencesRepository: UserSearchPreferencesRepository
) : ViewModel() {
    var responseUI: ResponseUI by mutableStateOf(ResponseUI.Loading)
        private set
    var searchText by mutableStateOf("")
        private set
    var darkMode: StateFlow<Boolean> = userSearchPreferencesRepository.userDarkPref
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), false)
    var departure: Airport? = null
    var flightState by mutableStateOf(FlightState())
        private set
    var suggestionState by mutableStateOf(SuggestionState())
        private set
    var favoriteState by mutableStateOf(FavoriteState())
        private set
    private var favoriteList = emptyList<Boolean>()
    private var job: Job? = null

    init {
        getFavoriteFlightOrSearchForFlights()
    }

    /**
     * Function for show all favorite flight if [searchText] is empty.
     * If not then execute [getAllFlightsByIataCodeOrName] and pass in the departure.
     */
    private fun getFavoriteFlightOrSearchForFlights() = viewModelScope.myLaunch {
        searchText = userSearchPreferencesRepository.userSearchPref
            .stateIn(viewModelScope).value
        Log.d("onSuggestion", "prefs: $searchText")

        //add logic for retrieve an airport by it iataCode or name
        if (searchText.isNotEmpty()) {
            val response = getAllSuggestionsByIataCodeOrName(searchText)
            if (response.isNotEmpty()) getAllFlightsByIataCodeOrName(response.first()) else getAllFavoriteFlight()
        } else {
            getAllFavoriteFlight()
        }
    }


    fun onEvent(event: HomeScreenEvent) {
        when (event) {
            /**
             * event block for each user input. Set [searchText] to input user and then execute [setUserSearch]
             * If [searchText] is empty then execute [getAllFavoriteFlight]
             * If not update [suggestionState] by execute [getAllSuggestionsByIataCodeOrName]
             */
            is HomeScreenEvent.InputSearch -> {
                searchText = event.input.also { setUserSearch(it) }
                if (searchText.isEmpty()) {
                    favoriteState = favoriteState.copy(isVisible = true)
                    suggestionState = suggestionState.copy(isVisible = false)
                    flightState = flightState.copy(isVisible = false)
                    getAllFavoriteFlight()
                } else {
                    job = null
                    job = viewModelScope.launch {
                        suggestionState = suggestionState.copy(
                            suggestionList = getAllSuggestionsByIataCodeOrName(event.input),
                            isVisible = true
                        )
                    }
                }
            }
            /**
             * event block for user favorite action.
             * if [event] is true then execute [favoriteRepository] for delete
             * if not execute [favoriteRepository] for insert.
             * if [favoriteState] is visible then update its items
             * if not update items for [flightState]
             */
            is HomeScreenEvent.ToFavoriteAirport -> {
                Log.d("onSuggestion", "onEvent: ${event.isFavorite}")
                if (event.isFavorite) {
                    viewModelScope.launch {
                        favoriteRepository.deleteFavorite(
                            Favorite(
                                departureCode = event.departure.iataCode,
                                destinationCode = event.destination.iataCode
                            )
                        )
                    }
                } else {
                    viewModelScope.launch {
                        favoriteRepository.insertFavorite(
                            Favorite(
                                departureCode = event.departure.iataCode,
                                destinationCode = event.destination.iataCode
                            )
                        )
                    }
                }
                if (favoriteState.isVisible) {
//                    favoriteState = favoriteState.copy(
//                        favoriteList =
//                        favoriteState.favoriteList.filter { it.departure != event.departure && it.destination != event.destination },
//                    )
                    getAllFavoriteFlight()
                } else {
                    flightState = flightState.copy(
                        flightUI = flightState.flightUI.map { item ->
                            item.takeIf { departure?.id == event.departure.id && it.destination.id == event.destination.id }
                                ?.let {
                                    it.copy(isFavorite = !it.isFavorite)
                                } ?: item
                        },
                        isVisible = true
                    )
                }
            }

            /**
             * event block for when user click on suggestion list item for [suggestionState]
             * Simply execute [getAllFlightsByIataCodeOrName]
             */
            is HomeScreenEvent.OnClickSuggestion -> {
                getAllFlightsByIataCodeOrName(event.departure)

                Log.d("onSuggestion", "flighUI: ${flightState.flightUI.size}")
                Log.d("onSuggestion", "favs: ${favoriteList.size}")
            }
            is HomeScreenEvent.ChangeAppColor -> {
                viewModelScope.myLaunch {
                    userSearchPreferencesRepository.setUserDarkModePreferences(event.darkMode)
                }
//
//                viewModelScope.launch {
//                    userSearchPreferencesRepository.setUserDarkModePreferences(event.darkMode)
//                }
            }
        }
        //need to continue search for a departure -> ok
        //need to mark a flight as favorite
    }


    /**
     * function for obtain all airport objects from [airportRepository]
     * then call [favoriteRepository] for [favoriteList] list
     * finally mapped it to [flightState]
     * also hidden [suggestionState] list and [favoriteState] list
     * also set [departure]
     */
    private fun getAllFlightsByIataCodeOrName(departureEvent: Airport) {
        suggestionState = suggestionState.copy(isVisible = false)
        favoriteState = favoriteState.copy(isVisible = false)
        departure = departureEvent
        job = null
        job = viewModelScope.myLaunch {
            flightState = flightState.copy(
                flightUI = airportRepository.getAllAirport()
                    .map { list ->
                        list.filter { it != departureEvent }
                            .also { items ->
                                Log.d("onSuggestion", "airportList: ${items.size}")
                                favoriteList = items.map {
                                    favoriteRepository.searchFavoriteFlightByDepartureAndDestination(
                                        departureEvent.iataCode,
                                        it.iataCode
                                    ).validateFavorite()
                                }
                            }
                    }
                    .stateIn(viewModelScope).value.mapIndexed { index, airport ->
                        FlightUI(
                            destination = airport,
                            isFavorite = favoriteList[index],
                        )
                    },
                isVisible = true
            )
        }
    }

    /**
     * function that return airport [List] from [airportRepository]
     * filtered by [input] user search
     */
    private suspend fun getAllSuggestionsByIataCodeOrName(input: String): List<Airport> {
        return airportRepository.getAirportByNameOrIataCode(
            name = "${input}%",
            iataCode = "${input}%"
        ).stateIn(viewModelScope).value
    }

    /**
     * function for set user preferences by call [userSearchPreferencesRepository]
     */
    private fun setUserSearch(userSearch: String) = viewModelScope.launch {
        userSearchPreferencesRepository.setUserSearchPreferences(userSearch)
    }


    /**
     *function for obtain all airport object from [airportRepository]
     * filtered for all favorite objects from [favoriteRepository]
     * finally mapped it to [favoriteState]
     */
    private fun getAllFavoriteFlight() {
        job = null
        job = viewModelScope.launch {

            favoriteState = favoriteState.copy(
                favoriteList = airportRepository.getAllAirport().map { airports ->
                    favoriteRepository.getAllFavorite().map { favorites ->
                        favorites.map { favorite -> //1,departure = opo,destination = arc
                            val destination =
                                airports.find { it.iataCode == favorite.destinationCode }!!
                            val departure =
                                airports.find { it.iataCode == favorite.departureCode }!!
                            FavoriteUI(
                                destination = destination,
                                departure = departure,
                            )
                        }
                    }.stateIn(viewModelScope).value
                }.stateIn(viewModelScope).value
            )
        }
    }

    /**
     * Extension function for view model coroutine scope that manipulates the [responseUI] accordingly.
     * @param block the suspend function content
     * @return a job attached to this scope
     */
    private fun CoroutineScope.myLaunch(block: suspend CoroutineScope.() -> Unit): Job {
        return launch {
            responseUI = ResponseUI.Loading
            block()
            responseUI = ResponseUI.Success
        }
    }
}

fun Int.validateFavorite() = this == 1