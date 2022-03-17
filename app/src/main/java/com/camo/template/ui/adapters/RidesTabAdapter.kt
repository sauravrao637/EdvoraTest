package com.camo.template.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.camo.template.ui.RidesFragment
import timber.log.Timber

class RidesTabAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    companion object {
        private const val TOTAL_TABS = 3
    }

    override fun getItemCount(): Int {
        return TOTAL_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when (position % TOTAL_TABS) {
            0 -> RidesFragment.newInstance(RidesFragment.RidesCategories.NEAREST)
            1 -> RidesFragment.newInstance(RidesFragment.RidesCategories.UPCOMING)
            2 -> RidesFragment.newInstance(RidesFragment.RidesCategories.PAST)
            else -> {
                Timber.e("ADD TABS")
                RidesFragment.newInstance(RidesFragment.RidesCategories.NEAREST)
            }
        }
    }

}