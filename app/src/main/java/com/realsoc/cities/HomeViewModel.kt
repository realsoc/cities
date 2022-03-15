package com.realsoc.cities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realsoc.cities.ui.CountriesTabState
import com.realsoc.cities.ui.FavoriteCitiesState
import com.realsoc.cities.ui.SearchTabState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {
    private val _searchTabState = MutableStateFlow<SearchTabState>(SearchTabState.Init)
    private val _countriesTabState = MutableStateFlow<CountriesTabState>(CountriesTabState.Init)
    private val _favoriteCitiesTabState = MutableStateFlow<FavoriteCitiesState>(FavoriteCitiesState.Init)

    private var searchCityJob: Job? = null

    private val inputEvents = Channel<String>(Channel.CONFLATED)

    val searchTabState: StateFlow<SearchTabState> = _searchTabState
    val countriesTabState: StateFlow<CountriesTabState> = _countriesTabState
    val favoriteCitiesState: StateFlow<FavoriteCitiesState> = _favoriteCitiesTabState

    init {
        observeCitySearch()
    }

    @OptIn(FlowPreview::class)
    private fun observeCitySearch() {
        viewModelScope.launch(Dispatchers.Default) {
            inputEvents.receiveAsFlow().onEach {
                val state = if (it == "") SearchTabState.Init else SearchTabState.Loading(it)
                _searchTabState.emit(state)
            }.debounce(500)
                .filter { it != "" }
                .distinctUntilChanged()
                .onEach(::performSearch)
                .retry {
                    it is CancellationException
                }
                .collect()
        }
    }

    private suspend fun performSearch(searchStr: String) {
        val job = viewModelScope.async(Dispatchers.Default) {
            kotlin.runCatching {
                _searchTabState.emit(SearchTabState.Loading(searchString = searchStr))

                Thread.sleep(2000)
                ensureActive()

                _searchTabState.emit(SearchTabState.Success(searchString = searchStr, listOf("City 1", "City 2")))
            }
        }
        searchCityJob = job
        try {
            job.await()
        } catch (e: CancellationException) {
            job.cancel()
            throw e
        }
    }

    fun searchCity(searchStr: String) {
        viewModelScope.launch(Dispatchers.Default) {
            searchCityJob?.cancel()
            inputEvents.send(searchStr)
        }
    }

    fun clearSearch() {

    }
}

