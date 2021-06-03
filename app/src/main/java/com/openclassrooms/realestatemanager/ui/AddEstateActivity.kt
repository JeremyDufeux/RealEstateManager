package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityAddEstateBinding

class AddEstateActivity : AppCompatActivity() {
    private val TAG = "AddEstateActivity"

    lateinit var mBinding: ActivityAddEstateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityAddEstateBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.activityAddEstateToolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_activity_toolbar_menu, menu)
        configureToolBar()
        return true
    }

    private fun configureToolBar(){
        mBinding.activityAddEstateToolbar.title = resources.getString(R.string.toolbar_title_add_estate)
        mBinding.activityAddEstateToolbar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_back_arrow, null)
        mBinding.activityAddEstateToolbar.setNavigationOnClickListener { finish() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save_estate -> saveEstate()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveEstate() {
        finish()
    }
}