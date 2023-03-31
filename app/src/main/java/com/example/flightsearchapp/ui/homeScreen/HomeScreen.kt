package com.example.flightsearchapp.ui.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flightsearchapp.data.local.database.airport.Airport
import com.example.flightsearchapp.ui.theme.FlightSearchAppTheme
import com.example.flightsearchapp.utils.ResponseUI

/**
 * Screen level composable for [HomeScreen]
 */

//@Composable
//fun HomeScreen(
//    modifier: Modifier = Modifier,
//    viewModel: HomeScreenViewModel = hiltViewModel(),
//    onColorChange: (Boolean) -> Unit
//) {
//    Box(modifier = modifier.fillMaxSize()) {
//
//        when (viewModel.responseUI) {
//            ResponseUI.Loading -> {
//        CircularProgressIndicator(progress = 0.5f)
////                CircularProgressIndicator (
////                        modifier = Modifier
////                            .align(Alignment.Center),
////                        color = Color.Blue
////                )
//            }
//            is ResponseUI.Error -> Unit
//            else -> {}
//        }
//    }
//}
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onColorChange: (Boolean) -> Unit
) {
    val flightState = viewModel.flightState
    val suggestionState = viewModel.suggestionState
    val favoriteState = viewModel.favoriteState
    val darkMode by viewModel.darkMode.collectAsState()

    //search textfield
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = viewModel.searchText,
                onValueChange = { viewModel.onEvent(HomeScreenEvent.InputSearch(it)) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "change drk mode",
                modifier = Modifier.clickable {
                           onColorChange(!darkMode)
                },
                tint = if (darkMode) MaterialTheme.colors.primary else MaterialTheme.colors.secondary
            )

        }
        Box(modifier = modifier.fillMaxSize()) {
            when (viewModel.responseUI) {
                is ResponseUI.Error -> Unit
                ResponseUI.Loading -> {
                    CircularProgressIndicator(
                        progress = 1f,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                ResponseUI.Success -> {
                    //lazy column for favorite flights
                    if (favoriteState.isVisible)
                        if (favoriteState.favoriteList.isEmpty()) {
                            Text(
                                text = "Very empty here.. Try mark a flight with ♥",
                                modifier = Modifier.align(Alignment.Center))
                        } else {
                            //show favorite list
                            ShowFavoriteList(
                                items = favoriteState.favoriteList,
                                onEvent = viewModel::onEvent
                            )
                        }

                    if (flightState.isVisible)
                    //lazy column for airport destination
                        ShowListDestinationResult(
                            items = flightState.flightUI,
                            departure = viewModel.departure!!,
                            onEvent = viewModel::onEvent,
                        )

                    if (suggestionState.isVisible) {
                        //lazy column for search suggestions
                        ShowListSuggestionsResult(
                            items = suggestionState.suggestionList,
                            onEvent = viewModel::onEvent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShowListSuggestionsResult(
    modifier: Modifier = Modifier,
    items: List<Airport>,
    onEvent: (HomeScreenEvent) -> Unit,
    ) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ){
        items(items = items, key = { it.id }){departure ->
            ShowSuggestionItem(
                departure = departure,
                onEvent = onEvent
            )
        }
    }
}

@Composable
fun ShowListDestinationResult(
    modifier: Modifier = Modifier,
    items: List<FlightUI>,
    departure: Airport,
    onEvent: (HomeScreenEvent) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ){
        item {
            Text(text = "Flight List", style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.height(20.dp))
        }
        items(items = items, key = { it.destination.id }) {flightUI ->
            ShowAirportCard(
                destination = flightUI.destination,
                departure = departure,
                isFavorite = flightUI.isFavorite,
                onEvent = onEvent
            )
        }
    }
}

@Composable
fun ShowAirportCard(
    modifier: Modifier = Modifier,
    destination: Airport,
    departure: Airport,
    isFavorite: Boolean,
    onEvent: (HomeScreenEvent) -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                AirportItem(airport = departure, typeFlight = "Departure")
                Spacer(modifier = Modifier.height(10.dp))
                AirportItem(airport = destination, typeFlight = "Destination")
            }
            IconButton(
                onClick = {
                    onEvent(HomeScreenEvent.ToFavoriteAirport(destination = destination, departure = departure, isFavorite))
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Favorite,
                    contentDescription = "favorite destination",
                    tint = if (isFavorite) MaterialTheme.colors.primary else MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}

@Composable
fun AirportItem(
    modifier: Modifier = Modifier,
    airport: Airport,
    typeFlight: String
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(text = typeFlight, style = MaterialTheme.typography.caption)
        Text(text = airport.name, style = MaterialTheme.typography.h5, maxLines = 3)
        Text(text = airport.iataCode, modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.End))
        Text(text = "${airport.passengers} passengers")
    }
}

@Composable
fun ShowFavoriteList(
    modifier: Modifier = Modifier,
    items: List<FavoriteUI>,
    onEvent: (HomeScreenEvent) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ){
        item {
            Text(text = "Favorite List", style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.height(20.dp))
        }
        items(items = items, key = { it.destination.id+it.departure.id}) {flightUI ->
            ShowAirportCard(
                destination = flightUI.destination,
                departure = flightUI.departure,
                isFavorite = flightUI.isFavorite,
                onEvent = onEvent
            )
        }
    }
}

@Composable
fun ShowSuggestionItem(
    modifier: Modifier = Modifier,
    departure: Airport,
    onEvent: (HomeScreenEvent) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(role = Role.Button) {
                onEvent(HomeScreenEvent.OnClickSuggestion(departure))
            },
    ) {
        Icon(imageVector = Icons.Outlined.Search, contentDescription = "search")
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = departure.iataCode, style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = departure.name, style = MaterialTheme.typography.body1)
    }
}

@Preview
@Composable
fun PrevComposable() {
    FlightSearchAppTheme() {
//        AirportItem(
//            airport = Airport(iataCode = "ARN", name = "Joseph Tarradelas Barcelona- El Prat Airport", passengers = 32907673),
//            typeFlight = "Departure"
//        )
//        ShowAirportCard(
//            destination = Airport(iataCode = "ARN", name = "Joseph Tarradelas Barcelona- El Prat Airport", passengers = 32907673),
//            departure = Airport(iataCode = "SOF", name = "Sofia Airport", passengers = 3354151)
//            )

//        ShowListDestinationResult(items = listOf(
//            Airport(iataCode = "SOF", name = "Sofia Airport", passengers = 3354151),
//        ), departure = Airport(iataCode = "DUB", name = "Dublin Airport", passengers = 3354151))
    }
}