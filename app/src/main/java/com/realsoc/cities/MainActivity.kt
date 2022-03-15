package com.realsoc.cities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.realsoc.cities.ui.HomeScreen
import com.realsoc.cities.ui.theme.CitiesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val homeViewModel: HomeViewModel by viewModels()

        setContent {
            CitiesTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    HomeScreen(
                        homeViewModel.searchTabState.collectAsState(),
                        homeViewModel.countriesTabState.collectAsState(),
                        homeViewModel.favoriteCitiesState.collectAsState(),
                        homeViewModel::searchCity
                    )
                }
            }
        }
    }
}