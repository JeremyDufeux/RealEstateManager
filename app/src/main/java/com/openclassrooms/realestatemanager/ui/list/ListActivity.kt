package com.openclassrooms.realestatemanager.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityListBinding
import com.openclassrooms.realestatemanager.ui.add.AddPropertyActivity
import com.openclassrooms.realestatemanager.ui.list.list.ListFragment
import com.openclassrooms.realestatemanager.ui.list.map.MapFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListActivity : AppCompatActivity() {
    private val TAG = "ListActivity"

    lateinit var mBinding : ActivityListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityListBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        configureToolBar()
        configureViewPager()
    }

    private fun configureToolBar() {
        setSupportActionBar(mBinding.activityListToolbar)
    }

    private fun configureViewPager(){
        val pagerAdapter = ListActivityViewPagerAdapter(supportFragmentManager, lifecycle)
        pagerAdapter.addFragment(ListFragment.newInstance())
        pagerAdapter.addFragment(MapFragment.newInstance())

        mBinding.activityListViewPager.adapter = pagerAdapter
        mBinding.activityListViewPager.isUserInputEnabled = false

        TabLayoutMediator(mBinding.activityListTabLayout, mBinding.activityListViewPager
        ) { tab, position ->
            when (position) {
                0 -> tab.text = resources.getString(R.string.list_tab)
                1 -> tab.text = resources.getString(R.string.map_tab)
            }
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_activity_toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_property -> openAddPropertyActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openAddPropertyActivity() {
        val addPropertyActivityIntent = Intent(this, AddPropertyActivity::class.java)
        startActivity(addPropertyActivityIntent)
    }
}