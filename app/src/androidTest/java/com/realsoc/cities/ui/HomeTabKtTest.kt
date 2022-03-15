package com.realsoc.cities.ui

import android.graphics.Bitmap
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.realsoc.cities.ui.theme.CitiesTheme
import org.junit.Rule
import org.junit.Test
import java.io.FileOutputStream

class HomeTabKtTest{
    @get:Rule
    val composeTestRule = createComposeRule()

    private val searchTabState = mutableStateOf<SearchTabState>(SearchTabState.Init)
    private val countriesTabState = mutableStateOf<CountriesTabState>(CountriesTabState.Init)
    private val favoriteCitiesState = mutableStateOf<FavoriteCitiesState>(FavoriteCitiesState.Init)

    @Test
    fun testLaunchingDefaultTab() {

        val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources

        initializeView()

        composeTestRule.onRoot().printToLog("Tree")

        composeTestRule.onNodeWithTag("search_city_field").assertExists()
        composeTestRule.onNodeWithTag("search_city_result_list").assertDoesNotExist()
        
        composeTestRule.onNodeWithTag("country_list").assertDoesNotExist()
        composeTestRule.onNodeWithTag("favorite_city_list").assertDoesNotExist()

        composeTestRule.onNodeWithTag("nothing_yet_label").assertExists()

        val searchText = resources.getString(HomeTab.Search.resourceId)
        val countriesText = resources.getString(HomeTab.Countries.resourceId)
        val favoritesText = resources.getString(HomeTab.Favorites.resourceId)
        composeTestRule.onNode(matcher = hasText(searchText).and(hasContentDescription(searchText))).assertIsSelected()
        composeTestRule.onNode(matcher = hasText(countriesText).and(hasContentDescription(countriesText))).assertIsNotSelected()
        composeTestRule.onNode(matcher = hasText(favoritesText).and(hasContentDescription(favoritesText))).assertIsNotSelected()
    }

    @Test
    fun testSwitchingTabToFavorite() {

        val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources

        val searchText = resources.getString(HomeTab.Search.resourceId)
        val countriesText = resources.getString(HomeTab.Countries.resourceId)
        val favoritesText = resources.getString(HomeTab.Favorites.resourceId)

        initializeView()

        composeTestRule.onRoot().printToLog("Tree")

        composeTestRule.onNode(hasText(favoritesText).and(isSelectable())).performClick()

        composeTestRule.onNodeWithTag("search_city_field").assertDoesNotExist()
        composeTestRule.onNodeWithTag("search_city_result_list").assertDoesNotExist()

        composeTestRule.onNodeWithTag("country_list").assertDoesNotExist()

        composeTestRule.onNodeWithTag("favorite_city_list").assertDoesNotExist()


        composeTestRule.onNodeWithTag("nothing_yet_label").assertExists()

        composeTestRule.onNode(matcher = hasText(searchText).and(hasContentDescription(searchText))).assertIsNotSelected()
        composeTestRule.onNode(matcher = hasText(countriesText).and(hasContentDescription(countriesText))).assertIsNotSelected()
        composeTestRule.onNode(matcher = hasText(favoritesText).and(hasContentDescription(favoritesText))).assertIsSelected()
    }

    @Test
    fun testSwitchingTabToCountries() {

        val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources

        val searchText = resources.getString(HomeTab.Search.resourceId)
        val countriesText = resources.getString(HomeTab.Countries.resourceId)
        val favoritesText = resources.getString(HomeTab.Favorites.resourceId)

        initializeView()

        composeTestRule.onRoot().printToLog("Tree")

        composeTestRule.onNode(hasText(countriesText).and(isSelectable())).performClick()

        composeTestRule.onNodeWithTag("search_city_field").assertDoesNotExist()
        composeTestRule.onNodeWithTag("search_city_result_list").assertDoesNotExist()
        composeTestRule.onNodeWithTag("country_list").assertDoesNotExist()

        composeTestRule.onNodeWithTag("nothing_yet_label").assertExists()


        composeTestRule.onNodeWithTag("favorite_city_list").assertDoesNotExist()

        composeTestRule.onNode(matcher = hasText(searchText).and(hasContentDescription(searchText))).assertIsNotSelected()
        composeTestRule.onNode(matcher = hasText(countriesText).and(hasContentDescription(countriesText))).assertIsSelected()
        composeTestRule.onNode(matcher = hasText(favoritesText).and(hasContentDescription(favoritesText))).assertIsNotSelected()
    }

    @Test
    fun testSwitchingTabAndReturningOnSearch() {

        val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources

        val searchText = resources.getString(HomeTab.Search.resourceId)
        val countriesText = resources.getString(HomeTab.Countries.resourceId)
        val favoritesText = resources.getString(HomeTab.Favorites.resourceId)

        initializeView()

        composeTestRule.onRoot().printToLog("Tree")

        composeTestRule.onNodeWithTag("search_city_field").assertExists()
        composeTestRule.onNodeWithTag("search_city_result_list").assertDoesNotExist()

        composeTestRule.onNodeWithTag("country_list").assertDoesNotExist()
        composeTestRule.onNodeWithTag("favorite_city_list").assertDoesNotExist()

        composeTestRule.onNodeWithTag("nothing_yet_label").assertExists()


        composeTestRule.onNode(matcher = hasText(searchText).and(hasContentDescription(searchText))).assertIsSelected()
        composeTestRule.onNode(matcher = hasText(countriesText).and(hasContentDescription(countriesText))).assertIsNotSelected()
        composeTestRule.onNode(matcher = hasText(favoritesText).and(hasContentDescription(favoritesText))).assertIsNotSelected()
    }

    private fun initializeView() {
        composeTestRule.setContent {
            CitiesTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    HomeScreen(
                        searchTabState = searchTabState,
                        countriesTabState = countriesTabState,
                        favoriteCitiesState = favoriteCitiesState,
                        {}
                    )
                }
            }
        }
    }
}

private fun saveScreenShot(fileName: String, image: Bitmap) {
    val path = InstrumentationRegistry.getInstrumentation().targetContext.filesDir.canonicalPath

    FileOutputStream("$path/$fileName.png").use { out ->
        image.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
    println("Saved screen shot at $path/$fileName.png")

}