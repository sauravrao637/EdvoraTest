package com.camo.template.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.camo.template.ui.viewmodels.MainActivityVM
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
        lifecycleScope.launchWhenStarted {
            val textV = findViewById<TextView>(R.id.tvUsername)
            val profileIv = findViewById<ImageView>(R.id.ivProfile)
            viewModel.userState.collect {
                Timber.d("$it")
                when (it.status) {
                    Status.SUCCESS -> {
                        textV.text = it.data?.name ?: "Error"
                        Glide.with(binding.root.context)
                            .load(it.data?.url)
                            .fitCenter()
                            .into(profileIv)
                    }
                    else -> {
                        textV.text = ""
                    }
                }
            }
        }
    }

    private fun setUpUi() {
        val adapter = RidesTabAdapter(this)
        binding.vpFragRides.adapter = adapter
        TabLayoutMediator(binding.tlFragRides, binding.vpFragRides) { tab, position ->
            when (position % adapter.itemCount) {
                0 -> tab.text = this.getString(R.string.nearest)
                1 -> tab.text = this.getString(R.string.upcoming)
                2 -> tab.text = this.getString(R.string.past)
                else -> {
                    tab.text = "ADD TEXT"
                }
            }
        }.attach()

        binding.btnFilters.setOnClickListener {
            binding.llFilters.visibility =
                if (binding.llFilters.visibility == View.GONE) View.VISIBLE else View.GONE
        }
    }
}