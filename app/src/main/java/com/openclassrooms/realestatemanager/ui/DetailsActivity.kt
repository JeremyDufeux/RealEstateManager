package com.openclassrooms.realestatemanager.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityDetailsBinding

class DetailsActivity : AppCompatActivity() {
    lateinit var mBinding : ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        configureToolBar()
    }

    private fun configureToolBar(){
        setSupportActionBar(mBinding.activityDetailsToolbar)
        mBinding.activityDetailsToolbar.title = resources.getString(R.string.toolbar_title_add_estate)
        mBinding.activityDetailsToolbar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_back_arrow, null)
        mBinding.activityDetailsToolbar.setNavigationOnClickListener { finish() }
    }
}