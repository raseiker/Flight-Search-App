package com.example.flightsearchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flightsearchapp.ui.homeScreen.HomeScreen
import com.example.flightsearchapp.ui.homeScreen.HomeScreenEvent
import com.example.flightsearchapp.ui.homeScreen.HomeScreenViewModel
import com.example.flightsearchapp.ui.theme.FlightSearchAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewmodel = hiltViewModel<HomeScreenViewModel>()
            FlightSearchAppTheme(darkTheme = viewmodel.darkMode.collectAsState().value) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold {
                        HomeScreen(
                            modifier = Modifier.padding(it),
                            viewModel = viewmodel,
                            onColorChange = { color -> viewmodel.onEvent(HomeScreenEvent.ChangeAppColor(color)) }
                        )
                    }
                }
            }
        }
    }
}