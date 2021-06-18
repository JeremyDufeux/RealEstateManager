package com.openclassrooms.realestatemanager.ui.add

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.chip.Chip
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityAddPropertyBinding
import com.openclassrooms.realestatemanager.models.PointsOfInterest
import com.openclassrooms.realestatemanager.models.PropertyType
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "AddPropertyActivity"

@AndroidEntryPoint
class AddPropertyActivity : AppCompatActivity() {
    private val mViewModel: AddActivityViewModel by viewModels()
    lateinit var mBinding: ActivityAddPropertyBinding

    override fun onCreate(savedInstancProperty: Bundle?) {
        super.onCreate(savedInstancProperty)

        mBinding = ActivityAddPropertyBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        configureUi()
        configureListeners()

        setSupportActionBar(mBinding.activityAddPropertyToolbar)
    }

    private fun configureUi() {
        val adapter : ArrayAdapter<PropertyType> = ArrayAdapter<PropertyType>(this, android.R.layout.simple_spinner_item, PropertyType.values())
        mBinding.activityAddPropertyTypeSp.adapter = adapter

        for (point in PointsOfInterest.values()){
            val chip = layoutInflater.inflate(
                R.layout.single_chip_layout,
                mBinding.activityAddPropertyPointOfInterestCg,
                false) as Chip
            chip.text = point.description
            chip.tag = point
            val image = ResourcesCompat.getDrawable(mBinding.root.context.resources, point.icon, null)
            chip.chipIcon = image
            chip.isCheckable = true
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked) {
                    mViewModel.pointOfInterestList.add(buttonView.tag as PointsOfInterest)
                } else{
                    mViewModel.pointOfInterestList.remove(buttonView.tag as PointsOfInterest)
                }
            }
            mBinding.activityAddPropertyPointOfInterestCg.addView(chip)

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configureListeners() {
        mBinding.apply {
            activityAddPropertyTypeSp.onItemSelectedListener = spinnerListener()
            activityAddPropertyPriceEt.addTextChangedListener(priceTextWatcher())
            activityAddPropertySurfaceEt.addTextChangedListener(surfaceTextWatcher())
            activityAddPropertyRoomsEt.addTextChangedListener(roomsTextWatcher())
            activityAddPropertyBathroomsEt.addTextChangedListener(bathroomsTextWatcher())
            activityAddPropertyBedroomsEt.addTextChangedListener(bedroomsTextWatcher())
            activityAddPropertyDescriptionEt.addTextChangedListener(descriptionTextWatcher())
            activityAddPropertyAddressLine1Et.addTextChangedListener(address1TextWatcher())
            activityAddPropertyAddressLine2Et.addTextChangedListener(address2TextWatcher())
            activityAddPropertyAddressCityEt.addTextChangedListener(cityTextWatcher())
            activityAddPropertyAddressPostalCodeEt.addTextChangedListener(postalCodeTextWatcher())
            activityAddPropertyAddressCountryEt.addTextChangedListener(countryTextWatcher())
            activityAddPropertyAgentEt.addTextChangedListener(agentTextWatcher())
            activityAddPropertySaveBtn.setOnClickListener { saveProperty() }
        }
    }

    private fun spinnerListener() = object: AdapterView.OnItemSelectedListener{
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (parent != null) {
                mViewModel.propertyType = parent.getItemAtPosition(position) as PropertyType
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>?) { }
    }

    private fun priceTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.price = s.toString().toInt()
            checkFields()
        }
    }

    private fun surfaceTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.surface = s.toString().toInt()
            checkFields()
        }
    }

    private fun roomsTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.room = s.toString().toInt()
            checkFields()
        }
    }

    private fun bathroomsTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.bathroom = s.toString().toInt()
            checkFields()
        }
    }

    private fun bedroomsTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.bedroom = s.toString().toInt()
            checkFields()
        }
    }

    private fun descriptionTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.description = s.toString()
            checkFields()
        }
    }

    private fun address1TextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.address1 = s.toString()
            checkFields()
        }
    }

    private fun address2TextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.address2 = s.toString()
        }
    }

    private fun cityTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.city = s.toString()
            checkFields()
        }
    }

    private fun postalCodeTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.postalCode = s.toString()
            checkFields()
        }
    }

    private fun countryTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.country = s.toString()
            checkFields()
        }
    }

    private fun agentTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.agent = s.toString()
            checkFields()
        }
    }

    private fun saveProperty(){
        mViewModel.saveProperty()
    }

    private fun checkFields(){
        mBinding.apply {
            val enableSaveBtn = activityAddPropertyPriceEt.text.isNotEmpty() &&
                    activityAddPropertySurfaceEt.text.isNotEmpty() &&
                    activityAddPropertyRoomsEt.text.isNotEmpty() &&
                    activityAddPropertyBathroomsEt.text.isNotEmpty() &&
                    activityAddPropertyBedroomsEt.text.isNotEmpty() &&
                    activityAddPropertyAddressLine1Et.text.isNotEmpty() &&
                    activityAddPropertyAddressCityEt.text.isNotEmpty() &&
                    activityAddPropertyAddressPostalCodeEt.text.isNotEmpty() &&
                    activityAddPropertyAddressCountryEt.text.isNotEmpty() &&
                    activityAddPropertyAgentEt.text.isNotEmpty()
            activityAddPropertySaveBtn.isEnabled = enableSaveBtn
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        configureToolBar()
        return true
    }

    private fun configureToolBar(){
        mBinding.activityAddPropertyToolbar.title = resources.getString(R.string.toolbar_title_add_property)
        mBinding.activityAddPropertyToolbar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_back_arrow, null)
        mBinding.activityAddPropertyToolbar.setNavigationOnClickListener { finish() }
    }
}