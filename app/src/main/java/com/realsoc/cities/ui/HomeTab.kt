package com.realsoc.cities.ui

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.realsoc.cities.R
import com.realsoc.cities.ui.theme.CitiesTheme
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Home Screen Composable.
 *
 * Material scaffold layout with a bottom bar to switch between the home tabs.
 */
@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    searchTabState: State<SearchTabState>,
    countriesTabState: State<CountriesTabState>,
    favoriteCitiesState: State<FavoriteCitiesState>,
    onTextChanged: (newText: String) -> Unit
) {
    val navController = rememberAnimatedNavController()
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                ORDERED_HOME_TABS.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.imageVector, contentDescription = stringResource(id = screen.resourceId)) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedNavHost(navController, startDestination = HomeTab.Search.route, Modifier.padding(innerPadding)) {
            composableFadeInAndOut(route = HomeTab.Search.route) { SearchCityContent(searchTabState, onTextChanged) }
            composableFadeInAndOut(route = HomeTab.Countries.route) { CountriesContent(countriesTabState) }
            composableFadeInAndOut(route = HomeTab.Favorites.route) { FavoriteCitiesContent(favoriteCitiesState) }
        }
    }
}

/**
 * Search city tab.
 */
@Composable
fun SearchCityContent(searchTabState: State<SearchTabState>, onTextChanged: (newText: String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val searchString = (searchTabState.value as? SearchTabState.Loading)?.searchString ?: (searchTabState.value as?
                SearchTabState.Success)?.searchString ?: ""
        SearchCityField(searchString, onTextChanged = onTextChanged)
        with (searchTabState.value) {
            when(this) {
                is SearchTabState.Success -> {
                    GeographicItemList(cities, Icons.Default.Favorite, testTag =
                    "search_city_result_list")
                }
                is SearchTabState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is SearchTabState.Error -> {
                    Error(error = exception)
                }
                is SearchTabState.Init -> {
                    NothingYet()
                }
            }
        }
    }
}

/**
 * Country list tab.
 */
@Composable
fun CountriesContent(countriesTabState: State<CountriesTabState>) {
    with(countriesTabState.value) {
        when(this) {
            is CountriesTabState.Success -> {
                GeographicItemList(countries, testTag = "country_list")
            }
            is CountriesTabState.Error -> {
                Error(error = exception)
            }
            is CountriesTabState.Init -> {
                NothingYet()
            }
        }
    }
}

/**
 * Favorite city list tab.
 */
@Composable
fun FavoriteCitiesContent(favoriteCitiesState: State<FavoriteCitiesState>) {
    with(favoriteCitiesState.value) {
        when(this) {
            is FavoriteCitiesState.Success -> {
                GeographicItemList(favoriteCities, Icons.Default.Favorite, testTag = "favorite_city_list")
            }
            is FavoriteCitiesState.Error -> {
                Error(error = exception)
            }
            is FavoriteCitiesState.Init -> {
                NothingYet()
            }
        }
    }
}

@Composable
fun NothingYet() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "N0THING Y3T", modifier = Modifier
            .align(Alignment.Center)
            .testTag("nothing_yet_label"), fontSize
        = 20.sp)
    }
}

@Composable
fun Error(error: Throwable) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "An error occurred ?",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp
        )
        Text(
            text = error.localizedMessage ?: "Unknown error",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 14.sp
        )
    }
}

/**
 * Add the [Composable] to the [NavGraphBuilder] with fade in and out transition
 *
 * @param route route for the destination
 * @param content composable for the destination
 */
@Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.composableFadeInAndOut(
    route: String,
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    val animDuration = 600
    composable(
        route,
        enterTransition = { fadeIn(animationSpec = tween(animDuration)) },
        exitTransition = { fadeOut(animationSpec = tween(animDuration)) },
        content = content
    )
}


/**
 * Composable showing a geographic item element
 */
@Composable
fun GeographicItemList(list: List<String>, imageVector: ImageVector? = null, testTag: String = "") {
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .testTag(testTag)) {
        itemsIndexed(list) { index, element ->
            GeographicItem(
                element,
                trailingIcon = imageVector?.let { ClickableIcon(imageVector, contentDescription = "Favorite") }
            )
            if (index != list.lastIndex) {
                Divider(modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
    }
}

/**
 * Clickable list item with title, optional subtitle and optional trailing icon, aimed at representing a geographic
 * place.
 */
@Composable
fun GeographicItem(
    title: String,
    subtitle: String? = null,
    trailingIcon: ClickableIcon? = null,
    onClick: () -> Unit = {}
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Max)
        .clickable(onClick = onClick)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                modifier = Modifier
                    .padding(start = 16.dp, top = 5.dp),
                textAlign = TextAlign.Start,
                fontSize = 20.sp
            )
            Text(
                text = subtitle ?: "",
                modifier = Modifier.padding(start = 24.dp, bottom = 5.dp),
                fontSize = 14.sp
            )
        }
        trailingIcon?.let { icon ->
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable(onClick = icon.onClick)
            ) {
                Icon(icon.imageVector, contentDescription = icon.contentDescription, modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

/**
 * Search city component.
 */
@Composable
fun SearchCityField(initText: String, onTextChanged: (newText: String) -> Unit) {
    var text: String by remember { mutableStateOf(initText) }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onTextChanged(it) },
        label = { Text("City") },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = { Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier
            .clickable(onClick = {})) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, top = 15.dp)
            .testTag("search_city_field"),
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.DarkGray)
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CitiesTheme {
        HomeScreen(
            MutableStateFlow(SearchTabState.Init).collectAsState(),
            MutableStateFlow(CountriesTabState.Init).collectAsState(),
            MutableStateFlow(FavoriteCitiesState.Init).collectAsState(),
            {}
        )
    }
}

/** Data structures */

/**
 * Data object in the home screen's scope, aimed at representing a geographic item.
 */
class GeographicItem(val title: String, val subtitle: String? = null)

sealed class HomeTab(val route: String, @StringRes val resourceId: Int, val imageVector: ImageVector) {
    object Search : HomeTab("profile", R.string.search, Icons.Default.Search)
    object Favorites : HomeTab("favorites", R.string.favorites, Icons.Default.Favorite)
    object Countries : HomeTab("countries", R.string.countries, Icons.Default.List)
}

val ORDERED_HOME_TABS = listOf(
    HomeTab.Search,
    HomeTab.Countries,
    HomeTab.Favorites
)

class ClickableIcon(
    val imageVector: ImageVector,
    val onClick: (() -> Unit) = {},
    val contentDescription: String = ""
)

sealed class CountriesTabState {
    object Init: CountriesTabState()
    data class Success(val countries: List<String>): CountriesTabState()
    data class Error(val exception: Throwable): CountriesTabState()
}

sealed class SearchTabState {
    object Init: SearchTabState()
    data class Success(val searchString: String, val cities: List<String>): SearchTabState()
    data class Loading(val searchString: String): SearchTabState()
    data class Error(val exception: Throwable): SearchTabState()
}

sealed class FavoriteCitiesState {
    object Init: FavoriteCitiesState()
    data class Success(val favoriteCities: List<String>): FavoriteCitiesState()
    data class Error(val exception: Throwable): FavoriteCitiesState()
}

class Country(val id: String, val name: String)
class UrbanDivision(val id: String, val name: String, val country: Country)
class City(val id: String, val name: String, val latLng: LatLng, val favorite: Boolean)
class LatLng(val lat: Float, lon: Float)