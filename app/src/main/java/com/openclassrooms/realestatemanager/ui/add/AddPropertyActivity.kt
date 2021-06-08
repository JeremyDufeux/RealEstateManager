package com.openclassrooms.realestatemanager.ui.add

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityAddPropertyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPropertyActivity : AppCompatActivity() {
    private val TAG = "AddPropertyActivity"

    lateinit var mBinding: ActivityAddPropertyBinding

    override fun onCreate(savedInstancProperty: Bundle?) {
        super.onCreate(savedInstancProperty)

        mBinding = ActivityAddPropertyBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.activityAddPropertyToolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_activity_toolbar_menu, menu)
        configureToolBar()
        return true
    }

    private fun configureToolBar(){
        mBinding.activityAddPropertyToolbar.title = resources.getString(R.string.toolbar_title_add_property)
        mBinding.activityAddPropertyToolbar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_back_arrow, null)
        mBinding.activityAddPropertyToolbar.setNavigationOnClickListener { finish() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save_property -> saveProperty()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveProperty() {
        finish()
    }
}