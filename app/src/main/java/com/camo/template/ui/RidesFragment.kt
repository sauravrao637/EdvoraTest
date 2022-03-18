package com.camo.template.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.camo.template.databinding.FragmentRidesBinding
import com.camo.template.ui.adapters.RidesAdapter
import com.camo.template.ui.viewmodels.MainActivityVM
import com.camo.template.util.Status
import kotlinx.coroutines.flow.collect
import timber.log.Timber

// the fragment initialization parameters
private const val FRAG_RIDES_CATEGORY = "rides_category_key"

/**
 * A simple [Fragment] subclass.
 * Use the [RidesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RidesFragment : Fragment() {
    private var binding: FragmentRidesBinding? = null
    private val viewModel: MainActivityVM by activityViewModels()
    private var ridesCategory: RidesCategories = RidesCategories.NEAREST
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ridesCategory = when (it.getString(FRAG_RIDES_CATEGORY)) {
                RidesCategories.UPCOMING.key -> RidesCategories.UPCOMING
                RidesCategories.NEAREST.key -> RidesCategories.NEAREST
                RidesCategories.PAST.key -> RidesCategories.PAST
                else -> {
                    Timber.e("Invalid Fragment key $it")
                    RidesCategories.UPCOMING
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRidesBinding.inflate(inflater, container, false)
        setupUI()
        setUpListeners()
        return binding?.root
    }

    private fun setUpListeners() {
        lifecycleScope.launchWhenStarted {
            viewModel.userState.collect {
                (binding?.rvRides?.adapter as RidesAdapter?)?.setUser(it.data)
                binding?.rvRides?.invalidate()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.ridesState.collect {
                with(it) {
                    when (status) {
                        Status.SUCCESS -> {
                            binding?.srl?.isRefreshing = false
                            if (data != null) {
                                binding?.rvRides?.adapter =
                                    RidesAdapter(
                                        data,
                                        viewModel.userState.value.data,
                                        ridesCategory, viewModel.filterByState.value,
                                        viewModel.filterByCity.value
                                    )
                                binding?.rvRides?.invalidate()
                            }
                        }
                        Status.ERROR -> {
                            binding?.srl?.isRefreshing = false
                        }
                        Status.IDLE -> {
                            binding?.srl?.isRefreshing = false
                        }
                        Status.LOADING -> {
                            binding?.srl?.isRefreshing = true
                        }
                    }
                }
            }
        }
        binding?.srl?.setOnRefreshListener {
            viewModel.refresh()
        }
        lifecycleScope.launchWhenStarted {
            viewModel.filterState.collect {
                (binding?.rvRides?.adapter as RidesAdapter?)?.setFilter(it)
                binding?.rvRides?.invalidate()
            }
        }
    }

    private fun setupUI() {
        binding?.rvRides?.layoutManager = LinearLayoutManager(context)
        binding?.rvRides?.adapter = RidesAdapter(
            viewModel.ridesState.value.data ?: arrayListOf(),
            viewModel.userState.value.data,
            ridesCategory,
            viewModel.filterByState.value,
            viewModel.filterByCity.value
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param ridesCategory Parameter 1
         * @return A new instance of fragment NearestRidesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(ridesCategory: RidesCategories) =
            RidesFragment().apply {
                arguments = Bundle().apply {
                    putString(FRAG_RIDES_CATEGORY, ridesCategory.key)
                }
            }
    }

    enum class RidesCategories(val key: String) {
        UPCOMING("upcoming"),
        NEAREST("nearest"),
        PAST("past")
    }
}