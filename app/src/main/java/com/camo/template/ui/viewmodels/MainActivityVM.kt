package com.camo.template.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camo.template.database.Repository
import com.camo.template.database.remote.model.Rides
import com.camo.template.database.remote.model.User
import com.camo.template.util.Resource
import com.camo.template.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

const val STATE = "state"
const val CITY = "city"

@HiltViewModel
class MainActivityVM @Inject constructor(
    private val cgRepo: Repository
) : ViewModel() {

    private val _ridesState = MutableStateFlow<Resource<Rides>>(Resource.idle())
    val ridesState: StateFlow<Resource<Rides>> get() = _ridesState

    private val _userState = MutableStateFlow<Resource<User>>(Resource.idle())
    val userState: StateFlow<Resource<User>> get() = _userState

    private val states = mutableListOf<String>()
    private val mapStateToCities = mutableMapOf<String, ArrayList<String>>()

//    private val _nearestRidesState = MutableStateFlow<Resource<Rides>>(Resource.idle())
//    val nearestRidesState:StateFlow<Resource<Rides>>get() = _nearestRidesState
//
//    private val _upcomingRidesState = MutableStateFlow<Resource<Rides>>()

    private val _filterByState = MutableStateFlow(STATE)
    val filterByState: StateFlow<String> get() = _filterByState
    private val _filterByCity = MutableStateFlow(CITY)
    val filterByCity: StateFlow<String> get() = _filterByCity

    val filterState = combine(_filterByCity, _filterByState) { a, b ->
        FilterState(a, b)
    }
    init {
        refresh()
    }

    private val _getUserJob: Job? = null
    private fun getUser() {
        _getUserJob?.cancel()
        _getRidesJob = viewModelScope.launch {
            cgRepo.getUserFlow().collect {
                Timber.d("user: $it")
                _userState.value = it
            }
        }
    }

    private var _getRidesJob: Job? = null
    private fun getRides() {
        states.clear()
        mapStateToCities.clear()
        _getRidesJob?.cancel()
        _getRidesJob = viewModelScope.launch {
            cgRepo.getRidesFlow().collect {
                _ridesState.value = it
                if (it.status == Status.SUCCESS && it.data != null) {
                    addStatesAndCities(it.data)
                }
            }
        }
    }


    private fun addStatesAndCities(rides: Rides) {
        if (rides.isEmpty()) return
        viewModelScope.launch {
            val statesSet = mutableSetOf<String>()
            for (ride in rides) {
                statesSet.add(ride.state.lowercase())
                if (mapStateToCities.containsKey(ride.state.lowercase())) {
                    mapStateToCities[ride.state.lowercase()]!!.add(ride.city.lowercase())
                } else {
                    mapStateToCities[ride.state.lowercase()] = arrayListOf(ride.city.lowercase())
                }
            }
            states.addAll(statesSet)
        }
    }

    fun initialize() {

    }

    fun refresh() {
        getRides()
        getUser()
    }

    fun setFilterByState(state: String) {

    }

}

class FilterState(val city: String, val state: String)
