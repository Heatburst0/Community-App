package com.kv.ablecommunity.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kv.ablecommunity.FollowersFragment
import com.kv.ablecommunity.FollowingFragment

class ViewPagerAdapter(fragmentActivity : FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0-> return FollowersFragment()
            1-> return FollowingFragment()

        }
        return FollowersFragment()
    }


}