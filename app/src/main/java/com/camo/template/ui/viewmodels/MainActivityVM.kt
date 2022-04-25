package com.camo.template.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camo.template.database.Repository
import com.camo.template.database.remote.model.Rides
import com.camo.template.database.remote.model.User
import com.camo.template.util.Resource
import com.camo.template.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
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
    var upcomingCount = 0
    var pastCount = 0
    private val _userState = MutableStateFlow<Resource<User>>(Resource.idle())
    val userState: StateFlow<Resource<User>> get() = _userState

    private val _statesFlow = MutableStateFlow(arrayListOf(STATE))
    val statesFlow: StateFlow<ArrayList<String>> get() = _statesFlow
    private val mapStateToCities = mutableMapOf<String, MutableSet<String>>()
    private val _citiesFlow = MutableStateFlow(mutableListOf(CITY))
    val citiesFlow: StateFlow<MutableList<String>> get() = _citiesFlow

    private val _filterByState = MutableStateFlow(STATE)
    val filterByState: StateFlow<String> get() = _filterByState
    private val _filterByCity = MutableStateFlow(CITY)
    val filterByCity: StateFlow<String> get() = _filterByCity

    val rideAndUserState: StateFlow<RideAndUserState> = combine(_ridesState,_userState){
        a,b -> RideAndUserState(b,a)
    }.stateIn(
        viewModelScope, started = SharingStarted.Eagerly, RideAndUserState(Resource.idle(),Resource.idle())
    )

    val filterState: StateFlow<FilterState> =
        combine(_filterByCity, _filterByState, _statesFlow, _citiesFlow) { a, b, c, d ->
            FilterState(a, b, c, d)
        }.stateIn(
            viewModelScope, started = SharingStarted.Eagerly, FilterState(
                CITY, STATE, mutableListOf(STATE),
                mutableListOf(CITY)
            )
        )

    init {
        refresh()
    }

    private val _getUserJob: Job? = null
    private fun getUser() {
        _getUserJob?.cancel()
        _getRidesJob = viewModelScope.launch {
            cgRepo.getUser().collect {
                Timber.d("user: $it")
                try {
                    _userState.value = it as Resource<User>
                } catch (e: Exception) {
                    _userState.value = Resource.error(null, "Error")
                }
            }
        }
    }

    private var _getRidesJob: Job? = null
    private fun getRides() {
        _getRidesJob?.cancel()
        _getRidesJob = viewModelScope.launch {
            cgRepo.getRides().collect {
                try {
                    _ridesState.value = it as Resource<Rides>
                    if (it.status == Status.SUCCESS && it.data != null) {
                        addStatesAndCities(it.data)
                    }
                } catch (e: Exception) {
                    _ridesState.value = Resource.error(null, errorInfo = "Error")
                }
            }
        }
    }


    private fun addStatesAndCities(rides: Rides) {
        if (rides.isEmpty()) return
        viewModelScope.launch {
            mapStateToCities.clear()
            upcomingCount = 0
            pastCount = 0
            val statesSet = mutableSetOf<String>()
            val citiesSet = mutableSetOf<String>()
            for (ride in rides) {
                if (ride.upcoming) upcomingCount += 1 else pastCount += 1
                statesSet.add(ride.state.lowercase())
                citiesSet.add(ride.city.lowercase())
                if (mapStateToCities.containsKey(ride.state.lowercase())) {
                    mapStateToCities[ride.state.lowercase()]!!.add(ride.city.lowercase())
                } else {
                    mapStateToCities[ride.state.lowercase()] = mutableSetOf(ride.city.lowercase())
                    mapStateToCities[ride.state.lowercase()]!!.add(CITY)
                }
            }
            val states = arrayListOf(STATE)
            states.addAll(statesSet)
            _statesFlow.value = states
            val cities = arrayListOf(CITY)
            cities.addAll(citiesSet)
            _citiesFlow.value = cities

            mapStateToCities[STATE] = mutableSetOf(CITY)
            mapStateToCities[STATE]?.addAll(citiesSet)
        }
    }

    fun initialize() {

    }

    fun refresh() {
        getRides()
        getUser()
    }

    fun setFilterByState(state: String) {
        val citiesList = mapStateToCities[state]!!.toMutableList()
        _citiesFlow.value = citiesList
        if(_filterByState.value!= state)_filterByCity.value = CITY
        _filterByState.value = state
    }

    fun setFilterByCity(city: String) {
        _filterByCity.value = city
    }

}

class RideAndUserState(val user: Resource<User>, val rides: Resource<Rides>)

class FilterState(
    val city: String,
    val state: String,
    val states: MutableList<String>,
    val cities: MutableList<String>
)
