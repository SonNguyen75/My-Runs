package com.namson_nguyen.myruns5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var startTabFragment: StartTabFragment
    private lateinit var historyTabFragment: HistoryTabFragment
    private lateinit var settingsTabFragment: SettingsTabFragment
    private lateinit var fragmentsArray: ArrayList<Fragment>
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabFragmentStateAdapter: TabFragmentStateAdapter
    private val tabTittles = arrayOf("Start", "History", "Settings")

    private lateinit var tabConfigurationStrategy: TabLayoutMediator.TabConfigurationStrategy
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startTabFragment = StartTabFragment()
        historyTabFragment = HistoryTabFragment()
        settingsTabFragment = SettingsTabFragment()

        fragmentsArray = ArrayList()
        fragmentsArray.add(startTabFragment)
        fragmentsArray.add(historyTabFragment)
        fragmentsArray.add(settingsTabFragment)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager2 = findViewById(R.id.viewPager)
        tabFragmentStateAdapter = TabFragmentStateAdapter(this, fragmentsArray)

        viewPager2.adapter = tabFragmentStateAdapter
        tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy(){            tab: TabLayout.Tab, position: Int ->
            tab.text = tabTittles[position]
        }
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()


    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }

}