package com.openclassrooms.realestatemanager.ui.add

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityAddPropertyBinding
import com.openclassrooms.realestatemanager.models.PointsOfInterest
import com.openclassrooms.realestatemanager.models.PropertyType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPropertyActivity : AppCompatActivity() {
    private val TAG = "AddPropertyActivity"

    lateinit var mBinding: ActivityAddPropertyBinding

    override fun onCreate(savedInstancProperty: Bundle?) {
        super.onCreate(savedInstancProperty)

        mBinding = ActivityAddPropertyBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        configureUi()

        setSupportActionBar(mBinding.activityAddPropertyToolbar)
    }

    private fun configureUi() {
        val adapter : ArrayAdapter<PropertyType> = ArrayAdapter<PropertyType>(this, android.R.layout.simple_spinner_item, PropertyType.values())
        mBinding.activityAddPropertyTypeSp.adapter = adapter
        mBinding.activityAddPropertyPriceEt.addTextChangedListener(PriceTextWatcher(mBinding.activityAddPropertyPriceEt))

        for (point in PointsOfInterest.values()){
            val chip = layoutInflater.inflate(R.layout.single_chip_layout,
                mBinding.activityAddPropertyPointOfInterestCg,
                false) as Chip
            chip.text = point.description
            chip.tag = point
            val image = ResourcesCompat.getDrawable(mBinding.root.context.resources, point.icon, null)
            chip.chipIcon = image
            chip.isCheckable = true
            mBinding.activityAddPropertyPointOfInterestCg.addView(chip)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_activity_toolbar_menu, menu)
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
            R.id.edit_property -> saveProperty()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveProperty() {
        finish()
    }
}