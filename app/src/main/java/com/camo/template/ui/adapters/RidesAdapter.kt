package com.camo.template.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.camo.template.R
import com.camo.template.database.remote.model.Ride
import com.camo.template.database.remote.model.Rides
import com.camo.template.database.remote.model.User
import com.camo.template.databinding.RideItemBinding
import com.camo.template.ui.RidesFragment
import com.camo.template.ui.viewmodels.CITY
import com.camo.template.ui.viewmodels.FilterState
import com.camo.template.ui.viewmodels.STATE
import java.lang.Integer.MAX_VALUE
import java.lang.Integer.min
import javax.inject.Inject
import kotlin.math.abs

class RidesAdapter @Inject constructor(
    private var list: Rides,
    private var user: User,
    private val ridesCategories: RidesFragment.RidesCategories,
    private var filterByState: String,
    private var filterByCity: String
) :
    RecyclerView.Adapter<RidesAdapter.ViewHolder>() {

    init {
        for (i in list) {
            i.dist = getDistanceForRide(i.stationPath)
        }
        list.sortBy { it.dist }
    }

    inner class ViewHolder(private val binding: RideItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Ride) {
            with(binding) {
                when (ridesCategories) {
                    RidesFragment.RidesCategories.UPCOMING -> {
                        if (item.upcoming && !filterOut(item)) {
                            showItem()
                        } else {
                            hideItem()
                        }
                    }
                    RidesFragment.RidesCategories.NEAREST -> {
                        if (!filterOut(item)) {
                            showItem()
                        } else {
                            hideItem()
                        }
                    }
                    RidesFragment.RidesCategories.PAST -> {
                        if (!item.upcoming && !filterOut(item)) {
                            showItem()
                        } else {
                            hideItem()
                        }
                    }
                }
                tvCityName.text = item.city
                tvRideId.text =
                    binding.root.context.getString(R.string.ride_idPrefix) + " ${item.id}"
                tvDistance.text =
                    binding.root.context.getString(R.string.distancePrefix) + " ${item.dist ?: "0"}"
                tvDate.text = binding.root.context.getString(R.string.datePrefix) + " ${item.date}"
                tvOriginStation.text =
                    binding.root.context.getString(R.string.origin_stationPrefix) + " ${item.originStationCode}"
                tvStateName.text = item.state
                tvStationPath.text =
                    binding.root.context.getString(R.string.station_pathPrefix) + " ${item.stationPath.toList()}"
                tvDistance.text = "${item.dist}"
                Glide.with(binding.root.context)
                    .load(item.mapUrl)
                    .fitCenter()
                    .into(ivMap)
            }
        }

        private fun RideItemBinding.showItem() {
            root.visibility = View.VISIBLE
            root.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        private fun filterOut(ride: Ride): Boolean {
            if (filterByCity == CITY && filterByState == STATE) return false
            if (filterByCity == CITY) {
                if (ride.state.lowercase() == filterByState.lowercase()) return false
            }
            if (filterByState == STATE) {
                if (ride.city.lowercase() == filterByCity.lowercase()) return false
            }
            if (ride.state.lowercase() == filterByState.lowercase() && ride.city.lowercase() == filterByCity.lowercase()) return false
            return true
        }
    }

    private fun RideItemBinding.hideItem() {
        root.visibility = View.GONE
        root.layoutParams = RecyclerView.LayoutParams(0, 0)
    }

    private fun getDistanceForRide(stationPath: List<Int>): Int {
        var dist = MAX_VALUE
        for (int in stationPath) {
            dist = min(dist, abs(int - user.stationCode).toInt())
        }
        return dist
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.ride_item, parent, false)
        val binding = RideItemBinding.bind(view)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(true)
        return holder.bind(list[position])
    }

    fun setFilter(filterState: FilterState) {
        this.filterByCity = filterState.city
        this.filterByState = filterState.state
        notifyDataSetChanged()
    }
}