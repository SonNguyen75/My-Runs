package com.namson_nguyen.myruns5


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabFragmentStateAdapter(activity: FragmentActivity, inputList : ArrayList<Fragment>) : FragmentStateAdapter(activity) {
    var fragmentList : ArrayList<Fragment>
    init {
        fragmentList = inputList
    }
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

}