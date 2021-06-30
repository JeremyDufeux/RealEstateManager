package com.openclassrooms.realestatemanager.ui.details

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

const val BUNDLE_KEY_PROPERTY_ID = "BUNDLE_KEY_PROPERTY_ID"

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {
    lateinit var mBinding : ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mBinding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.activityDetailsToolbar)
        configureFragment()
    }

    private fun configureFragment() {
        val detailFragment : DetailsFragment = supportFragmentManager.findFragmentById(R.id.activity_details_fragmentContainer) as DetailsFragment
        val propertyId = intent?.extras?.getString(BUNDLE_KEY_PROPERTY_ID)

        if (propertyId != null) {
            detailFragment.setPropertyId(propertyId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_activity_toolbar_menu, menu)
        configureToolBar()
        return true
    }

    private fun configureToolBar(){
        mBinding.activityDetailsToolbar.title = resources.getString(R.string.toolbar_title_property_details)

        mBinding.activityDetailsToolbar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_back_arrow, null)
        mBinding.activityDetailsToolbar.setNavigationOnClickListener { onBackPressed()}
    }
}