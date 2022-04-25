package com.camo.template.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.camo.template.R
import com.camo.template.databinding.ActivityMainBinding
import com.camo.template.ui.adapters.RidesTabAdapter
import com.camo.template.ui.viewmodels.CITY
import com.camo.template.ui.viewmodels.MainActivityVM
import com.camo.template.ui.viewmodels.STATE
import com.camo.template.util.Status
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityVM by viewModels()
    private var actionBar: ActionBar? = null
    private var stateSpinnerAdapter: ArrayAdapter<String>? = null
    private var citySpinnerAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(
            LayoutInflater.from(this)
        )
        Timber.i("Main Activity launched")

        actionBar = this.supportActionBar
        actionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar?.setDisplayShowCustomEnabled(true)
        actionBar?.setCustomView(R.layout.custom_action_bar)

        setUpUi()
        setContentView(binding.root)
        setUpListeners()
        viewModel.initialize()
    }

    private fun setUpListeners() {
//        lifecycleScope.launchWhenStarted {
//            val textV = findViewById<TextView>(R.id.tvUsername)
//            val profileIv = findViewById<ImageView>(R.id.ivProfile)
//            viewModel.userState.collect {
//                when (it.status) {
//                    Status.SUCCESS -> {
//                        textV.text = it.data?.name ?: "Error"
//                        Glide.with(binding.root.context)
//                            .load(it.data?.url)
//                            .fitCenter()
//                            .into(profileIv)
//                    }
//                    Status.LOADING -> {
//                        textV.text = "Loading"
//                    }
//                    Status.ERROR ->{
//                        textV.text = "ERROR"
//                    }
//                    Status.IDLE ->{
//                        textV.text = ""
//                    }
//                }
//            }
//        }
//        lifecycleScope.launchWhenStarted {
//            viewModel.ridesState.collect {
//                binding.tlFragRides.getTabAt(1)?.text =
//                    this@MainActivity.getString(R.string.upcoming) + " (${viewModel.upcomingCount})"
//                binding.tlFragRides.getTabAt(2)?.text =
//                    this@MainActivity.getString(R.string.past) + " (${viewModel.pastCount})"
//            }
//        }

        lifecycleScope.launchWhenStarted {
            viewModel.rideAndUserState.collect {
                val textV = findViewById<TextView>(R.id.tvUsername)
                val profileIv = findViewById<ImageView>(R.id.ivProfile)
                when {
                    it.user.status == Status.SUCCESS && it.rides.status == Status.SUCCESS -> {
                        if (it.rides.data != null && it.user.data != null) {
                            textV.text = it.user.data.name ?: "Error"
                            Glide.with(binding.root.context)
                                .load(it.user.data.url)
                                .fitCenter()
                                .into(profileIv)
                            binding.tlFragRides.getTabAt(1)?.text =
                                this@MainActivity.getString(R.string.upcoming) + " (${viewModel.upcomingCount})"
                            binding.tlFragRides.getTabAt(2)?.text =
                                this@MainActivity.getString(R.string.past) + " (${viewModel.pastCount})"
                        } else {
                            textV.text = "ERROR"
                        }
                    }
                    it.user.status == Status.ERROR || it.rides.status == Status.ERROR -> {
                        textV.text = "ERROR"
                    }
                    it.user.status == Status.LOADING || it.rides.status == Status.LOADING -> {
                        textV.text = "Loading"
                    }
                    else -> {
                        textV.text = ""
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.filterState.collect {
                stateSpinnerAdapter?.apply {
                    clear()
                    addAll(it.states)
                    notifyDataSetChanged()
                }
                citySpinnerAdapter?.apply {
                    clear()
                    addAll(it.cities)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun setUpUi() {
        viewModel.refresh()
        val adapter = RidesTabAdapter(this)
        binding.vpFragRides.adapter = adapter
        TabLayoutMediator(binding.tlFragRides, binding.vpFragRides) { tab, position ->
            when (position % adapter.itemCount) {
                0 -> tab.text = this.getString(R.string.nearest)
                1 -> tab.text = this.getString(R.string.upcoming) + " (${viewModel.upcomingCount})"
                2 -> tab.text = this.getString(R.string.past) + " (${viewModel.pastCount})"
                else -> {
                    tab.text = "ADD TEXT"
                }
            }
        }.attach()

        binding.btnFilters.setOnClickListener {
            binding.llFilters.visibility =
                if (binding.llFilters.visibility == View.GONE) View.VISIBLE else View.GONE
        }
        stateSpinnerAdapter = ArrayAdapter<String>(
            applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            viewModel.statesFlow.value
        )
        citySpinnerAdapter = ArrayAdapter<String>(
            applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            viewModel.citiesFlow.value
        )
        binding.spinnerState.apply {
            setAdapter(stateSpinnerAdapter)
            setText(STATE, false)
            onItemClickListener =
                OnItemClickListener { parent, view, position, id ->
                    viewModel.setFilterByState(stateSpinnerAdapter?.getItem(position) ?: STATE)
                    binding.spinnerCity.setText(CITY, false)
                }
        }
        binding.spinnerCity.apply {
            setAdapter(citySpinnerAdapter)
            setText(CITY, false)
            onItemClickListener =
                OnItemClickListener { parent, view, position, id ->
                    viewModel.setFilterByCity(citySpinnerAdapter?.getItem(position) ?: CITY)
                }
        }
    }
}