package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityListBinding

class ListActivity : AppCompatActivity() {
    private val TAG = "ListActivity"

    lateinit var mBinding : ActivityListBinding
    lateinit var toolbar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityListBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        toolbar = mBinding.listActivityToolbar
        setSupportActionBar(toolbar)

        val listFragment = ListFragment.newInstance()
        val mapFragment = MapFragment.newInstance()

        val pagerAdapter = ListActivityViewPagerAdapter(supportFragmentManager, lifecycle)
        pagerAdapter.addFragment(listFragment)
        pagerAdapter.addFragment(mapFragment)

        mBinding.detailsActivityViewPager.adapter = pagerAdapter

        TabLayoutMediator(mBinding.listActivityTabLayout, mBinding.detailsActivityViewPager, object  : TabLayoutMediator.TabConfigurationStrategy{
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                when(position) {
                    0 -> tab.text = resources.getString(R.string.list_tab)
                    1 -> tab.text = resources.getString(R.string.map_tab)
                }

            }
        }).attach()
    }


}