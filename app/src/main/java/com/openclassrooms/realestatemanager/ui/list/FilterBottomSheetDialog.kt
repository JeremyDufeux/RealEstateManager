package com.openclassrooms.realestatemanager.ui.list

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.BottomSheetDialogFilterBinding
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import com.openclassrooms.realestatemanager.utils.extensions.setFormattedNumber
import com.openclassrooms.realestatemanager.utils.formatTimestampToString

class FilterBottomSheetDialog: BottomSheetDialogFragment() {

    private val mViewModel: ListViewModel by activityViewModels()
    private var mBinding: BottomSheetDialogFilterBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = BottomSheetDialogFilterBinding.inflate(inflater)

        configureUi()

        return mBinding!!.root
    }

    private fun configureUi() {
        mBinding?.apply {
            // Apply
            filterBottomSheetApplyIb.setOnClickListener {
                mViewModel.applyFilter()
                dismiss()
            }

            // Types
            filterBottomSheetTypeCg.removeAllViews()
            PropertyType.values().map {
                val chip = layoutInflater.inflate(
                    R.layout.single_chip_layout,
                    filterBottomSheetTypeCg,
                    false
                ) as Chip
                chip.text = context?.getString(it.description)
                chip.tag = it
                chip.setChipIconTintResource( R.color.colorAccent)
                chip.isCheckable = true
                chip.isChecked = mViewModel.propertyFilter.propertyTypeList.contains(it)

                filterBottomSheetTypeCg.addView(chip)

                chip.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                       mViewModel.propertyFilter.propertyTypeList.add(buttonView.tag as PropertyType)
                    } else {
                       mViewModel.propertyFilter.propertyTypeList.remove(buttonView.tag as PropertyType)
                    }
                }
            }

            // City
            filterBottomSheetCityEtInput.setText(mViewModel.propertyFilter.city)
            filterBottomSheetCityEt.isEndIconVisible = (mViewModel.propertyFilter.city.isNotEmpty())
            filterBottomSheetCityEtInput.addTextChangedListener {
                mViewModel.propertyFilter.city = it.toString()
                filterBottomSheetCityEt.isEndIconVisible = (mViewModel.propertyFilter.city.isNotEmpty())
            }
            filterBottomSheetCityEt.setEndIconOnClickListener {
                mViewModel.propertyFilter.city = ""
                filterBottomSheetCityEtInput.setText("")
                filterBottomSheetCityEt.isEndIconVisible = false
            }

            // Price
            filterBottomSheetPriceMinEt.suffixText = getString(mViewModel.propertyFilter.userData.currency.symbolResId)
            if(mViewModel.propertyFilter.minPrice != 0L) {
                filterBottomSheetPriceMinEtInput.setFormattedNumber(
                    mViewModel.propertyFilter.minPrice.toBigDecimal().toPlainString()
                )
            }
            filterBottomSheetPriceMinEtInput.addTextChangedListener( object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    if(s.toString().isNotEmpty()) {
                        try {
                            mViewModel.propertyFilter.minPrice = s.toString().replace(" ", "").toLong()
                        } catch (e: Exception){
                            mViewModel.propertyFilter.minPrice = 0
                        }
                    } else {
                        mViewModel.propertyFilter.minPrice = 0
                    }
                }
            })
            filterBottomSheetPriceMinEtInput.doAfterTextChanged { text ->
                filterBottomSheetPriceMinEtInput.setFormattedNumber(text.toString())
            }

            filterBottomSheetPriceMaxEt.suffixText = getString(mViewModel.propertyFilter.userData.currency.symbolResId)
            if(mViewModel.propertyFilter.maxPrice != 0L) {
                filterBottomSheetPriceMaxEtInput.setFormattedNumber(
                    mViewModel.propertyFilter.maxPrice.toBigDecimal().toPlainString()
                )
            }
            filterBottomSheetPriceMaxEtInput.addTextChangedListener( object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    if(s.toString().isNotEmpty()) {
                        try {
                            mViewModel.propertyFilter.maxPrice = s.toString().replace(" ", "").toLong()
                        } catch (e: Exception){
                            mViewModel.propertyFilter.maxPrice = 0
                        }
                    } else {
                        mViewModel.propertyFilter.maxPrice = 0
                    }
                }
            })
            filterBottomSheetPriceMaxEtInput.doAfterTextChanged { text ->
                filterBottomSheetPriceMaxEtInput.setFormattedNumber(text.toString())
            }

            // Surface
            filterBottomSheetSurfaceMinEt.suffixText = getString(mViewModel.propertyFilter.userData.unit.abbreviationRsId)
            if(mViewModel.propertyFilter.minSurface != 0L) {
                filterBottomSheetSurfaceMinEtInput.setFormattedNumber(
                    mViewModel.propertyFilter.minSurface.toBigDecimal().toPlainString()
                )
            }
            filterBottomSheetSurfaceMinEtInput.addTextChangedListener( object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    if(s.toString().isNotEmpty()) {
                        try {
                            mViewModel.propertyFilter.minSurface = s.toString().replace(" ", "").toLong()
                        } catch (e: Exception){
                            mViewModel.propertyFilter.minSurface = 0
                        }
                    } else {
                        mViewModel.propertyFilter.minSurface = 0
                    }
                }
            })
            filterBottomSheetSurfaceMinEtInput.doAfterTextChanged { text ->
                filterBottomSheetSurfaceMinEtInput.setFormattedNumber(text.toString())
            }

            filterBottomSheetSurfaceMaxEt.suffixText = getString(mViewModel.propertyFilter.userData.unit.abbreviationRsId)
            if(mViewModel.propertyFilter.maxSurface != 0L) {
                filterBottomSheetSurfaceMaxEtInput.setFormattedNumber(
                    mViewModel.propertyFilter.maxSurface.toBigDecimal().toPlainString()
                )
            }
            filterBottomSheetSurfaceMaxEtInput.addTextChangedListener( object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    if(s.toString().isNotEmpty()) {
                        try {
                            mViewModel.propertyFilter.maxSurface = s.toString().replace(" ", "").toLong()
                        } catch (e: Exception){
                            mViewModel.propertyFilter.maxSurface = 0
                        }
                    } else {
                        mViewModel.propertyFilter.maxSurface = 0
                    }
                }
            })
            filterBottomSheetSurfaceMaxEtInput.doAfterTextChanged { text ->
                filterBottomSheetSurfaceMaxEtInput.setFormattedNumber(text.toString())
            }

            // Rooms
            filterBottomSheetRoomTv.text = mViewModel.propertyFilter.roomsAmount.toString()
            filterBottomSheetDecreaseRoomIb.isEnabled = (mViewModel.propertyFilter.roomsAmount != 0)
            filterBottomSheetDecreaseRoomIb.setOnClickListener {
               mViewModel.propertyFilter.roomsAmount--
                filterBottomSheetRoomTv.text = mViewModel.propertyFilter.roomsAmount.toString()
                filterBottomSheetDecreaseRoomIb.isEnabled = (mViewModel.propertyFilter.roomsAmount != 0)
            }
            filterBottomSheetIncreaseRoomIb.setOnClickListener {
               mViewModel.propertyFilter.roomsAmount++
                filterBottomSheetRoomTv.text = mViewModel.propertyFilter.roomsAmount.toString()
                filterBottomSheetDecreaseRoomIb.isEnabled = true
            }

            // Bathrooms
            filterBottomSheetBathroomTv.text = mViewModel.propertyFilter.bathroomsAmount.toString()
            filterBottomSheetDecreaseBathroomIb.isEnabled = (mViewModel.propertyFilter.bathroomsAmount != 0)
            filterBottomSheetDecreaseBathroomIb.setOnClickListener {
               mViewModel.propertyFilter.bathroomsAmount--
                filterBottomSheetBathroomTv.text = mViewModel.propertyFilter.bathroomsAmount.toString()
                filterBottomSheetDecreaseBathroomIb.isEnabled = (mViewModel.propertyFilter.bathroomsAmount != 0)
            }
            filterBottomSheetIncreaseBathroomIb.setOnClickListener {
               mViewModel.propertyFilter.bathroomsAmount++
                filterBottomSheetBathroomTv.text = mViewModel.propertyFilter.bathroomsAmount.toString()
                filterBottomSheetDecreaseBathroomIb.isEnabled = true
            }

            // Bedrooms
            filterBottomSheetBedroomTv.text = mViewModel.propertyFilter.bedroomsAmount.toString()
            filterBottomSheetDecreaseBedroomIb.isEnabled = (mViewModel.propertyFilter.bedroomsAmount != 0)
            filterBottomSheetDecreaseBedroomIb.setOnClickListener {
               mViewModel.propertyFilter.bedroomsAmount--
                filterBottomSheetBedroomTv.text = mViewModel.propertyFilter.bedroomsAmount.toString()
                filterBottomSheetDecreaseBedroomIb.isEnabled = (mViewModel.propertyFilter.bedroomsAmount != 0)
            }
            filterBottomSheetIncreaseBedroomIb.setOnClickListener {
               mViewModel.propertyFilter.bedroomsAmount++
                filterBottomSheetBedroomTv.text = mViewModel.propertyFilter.bedroomsAmount.toString()
                filterBottomSheetDecreaseBedroomIb.isEnabled = true
            }

            // Medias
            filterBottomSheetMediasTv.text = mViewModel.propertyFilter.mediasAmount.toString()
            filterBottomSheetDecreaseMediasIb.isEnabled = (mViewModel.propertyFilter.mediasAmount != 0)
            filterBottomSheetDecreaseMediasIb.setOnClickListener {
               mViewModel.propertyFilter.mediasAmount--
                filterBottomSheetMediasTv.text = mViewModel.propertyFilter.mediasAmount.toString()
                filterBottomSheetDecreaseMediasIb.isEnabled = (mViewModel.propertyFilter.mediasAmount != 0)
            }
            filterBottomSheetIncreaseMediasIb.setOnClickListener {
               mViewModel.propertyFilter.mediasAmount++
                filterBottomSheetMediasTv.text = mViewModel.propertyFilter.mediasAmount.toString()
                filterBottomSheetDecreaseMediasIb.isEnabled = true
            }

            // Poi
            filterBottomSheetPoiCg.removeAllViews()
            PointOfInterest.values().map {
                val chip = layoutInflater.inflate(
                    R.layout.single_chip_layout,
                    filterBottomSheetPoiCg,
                    false
                ) as Chip
                chip.text = context?.getString(it.description)
                chip.tag = it
                chip.setChipIconTintResource( R.color.colorAccent)
                chip.isCheckable = true
                chip.isChecked = mViewModel.propertyFilter.pointOfInterestList.contains(it)

                filterBottomSheetPoiCg.addView(chip)

                chip.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                       mViewModel.propertyFilter.pointOfInterestList.add(buttonView.tag as PointOfInterest)
                    } else {
                       mViewModel.propertyFilter.pointOfInterestList.remove(buttonView.tag as PointOfInterest)
                    }
                }
            }

            // Available check box
            filterBottomSheetAvailableCb.isChecked = mViewModel.propertyFilter.available
            filterBottomSheetAvailableCb.setOnCheckedChangeListener { _, isChecked ->
               mViewModel.propertyFilter.available = isChecked
                toggleView(filterBottomSheetPostDateEt, isChecked)

                if(isChecked && mViewModel.propertyFilter.sold){
                    mViewModel.propertyFilter.sold = false
                    filterBottomSheetSoldCb.isChecked = false
                    mViewModel.propertyFilter.soldDate = 0L
                }
            }

            // Post field
            if(mViewModel.propertyFilter.postDate != 0L){
                filterBottomSheetPostDateEtInput.setText(formatTimestampToString(mViewModel.propertyFilter.postDate))
            } else {
                filterBottomSheetPostDateEtInput.setText("")
            }
            if(mViewModel.propertyFilter.available){
                filterBottomSheetPostDateEt.visibility = View.VISIBLE
            }
            filterBottomSheetPostDateEtInput.setOnClickListener { openPostDatePicker() }
            filterBottomSheetPostDateEt.setEndIconOnClickListener { clearPostDatePicker() }
            filterBottomSheetPostDateEt.isEndIconVisible = (mViewModel.propertyFilter.soldDate != 0L)

            // Sold check box
            filterBottomSheetSoldCb.isChecked = mViewModel.propertyFilter.sold
            filterBottomSheetSoldCb.setOnCheckedChangeListener { _, isChecked ->
               mViewModel.propertyFilter.sold = isChecked
                toggleView(filterBottomSheetSoldDateEt, isChecked)

                if(isChecked && mViewModel.propertyFilter.available){
                   mViewModel.propertyFilter.available = false
                    filterBottomSheetAvailableCb.isChecked = false
                    mViewModel.propertyFilter.postDate = 0L
                }
            }

            // Sold text view
            if(mViewModel.propertyFilter.soldDate != 0L){
                filterBottomSheetSoldDateEtInput.setText(formatTimestampToString(mViewModel.propertyFilter.soldDate))
            } else {
                filterBottomSheetSoldDateEtInput.setText("")
            }
            if(mViewModel.propertyFilter.sold){
                filterBottomSheetSoldDateEt.visibility = View.VISIBLE
            }
            filterBottomSheetSoldDateEtInput.setOnClickListener { openSoldDatePicker() }
            filterBottomSheetSoldDateEt.setEndIconOnClickListener { clearSoldDatePicker() }
            filterBottomSheetSoldDateEt.isEndIconVisible = (mViewModel.propertyFilter.soldDate != 0L)

            // Clear filter
            filterBottomSheetClearBtn.setOnClickListener { resetFilters() }
        }
    }

    private fun clearPostDatePicker() {
       mViewModel.propertyFilter.soldDate = 0
        mBinding?.apply {
            filterBottomSheetPostDateEtInput.setText("")
            filterBottomSheetPostDateEt.isEndIconVisible = false
        }
    }

    private fun openPostDatePicker() {
        val date = if(mViewModel.propertyFilter.postDate != 0L){
            mViewModel.propertyFilter.postDate
        } else {
            MaterialDatePicker.todayInUtcMilliseconds()
        }

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_estates_posted_since    ))
            .setSelection(date)
            .setTheme(R.style.datePicker)
            .build()
        datePicker.show(parentFragmentManager, "PostDatePicker")
        datePicker.addOnPositiveButtonClickListener {
           mViewModel.propertyFilter.postDate = it

            mBinding?.apply {
                filterBottomSheetPostDateEtInput.setText(formatTimestampToString(it))
                filterBottomSheetPostDateEt.isEndIconVisible = true
            }
        }
    }

    private fun clearSoldDatePicker() {
       mViewModel.propertyFilter.soldDate = 0
        mBinding?.apply {
            filterBottomSheetSoldDateEtInput.setText("")
            filterBottomSheetSoldDateEt.isEndIconVisible = false
        }
    }

    private fun openSoldDatePicker() {
        val date = if(mViewModel.propertyFilter.soldDate != 0L){
            mViewModel.propertyFilter.soldDate
        } else {
            MaterialDatePicker.todayInUtcMilliseconds()
        }

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_estates_sold_since))
            .setSelection(date)
            .setTheme(R.style.datePicker)
            .build()
        datePicker.show(parentFragmentManager, "SoldDatePicker")
        datePicker.addOnPositiveButtonClickListener {
            mViewModel.propertyFilter.soldDate = it
            mBinding?.apply {
                filterBottomSheetSoldDateEtInput.setText(formatTimestampToString(it))
                filterBottomSheetSoldDateEt.isEndIconVisible = true
            }
        }
    }

    private fun resetFilters() {
        mViewModel.resetFilters()

        mBinding?.apply {
            for (id in filterBottomSheetTypeCg.checkedChipIds){
                filterBottomSheetTypeCg.findViewById<Chip>(id).isChecked = false
            }
            filterBottomSheetPriceMinEtInput.setText("")
            filterBottomSheetPriceMaxEtInput.setText("")
            filterBottomSheetSurfaceMinEtInput.setText("")
            filterBottomSheetSurfaceMaxEtInput.setText("")

            filterBottomSheetMediasTv.text = mViewModel.propertyFilter.roomsAmount.toString()
            filterBottomSheetRoomTv.text = mViewModel.propertyFilter.roomsAmount.toString()
            filterBottomSheetBathroomTv.text = mViewModel.propertyFilter.bathroomsAmount.toString()
            filterBottomSheetBedroomTv.text = mViewModel.propertyFilter.bedroomsAmount.toString()

            filterBottomSheetDecreaseMediasIb.isEnabled = false
            filterBottomSheetDecreaseRoomIb.isEnabled = false
            filterBottomSheetDecreaseBathroomIb.isEnabled = false
            filterBottomSheetDecreaseBedroomIb.isEnabled = false

            for (id in filterBottomSheetPoiCg.checkedChipIds){
                filterBottomSheetPoiCg.findViewById<Chip>(id).isChecked = false
            }

            filterBottomSheetCityEtInput.setText(mViewModel.propertyFilter.city)
            filterBottomSheetCityEtInput.clearFocus()

            filterBottomSheetAvailableCb.isChecked = false
            filterBottomSheetSoldCb.isChecked = false

            filterBottomSheetPostDateEtInput.setText("")
            filterBottomSheetSoldDateEtInput.setText("")

            filterBottomSheetPostDateEt.isEndIconVisible = false
            filterBottomSheetSoldDateEt.isEndIconVisible = false
        }
    }

    private fun toggleView(view: View, show: Boolean){
        if(show){
            view.alpha = 0f
            view.visibility = View.VISIBLE
            view.animate().alpha(1.0f).setDuration(1000).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    view.visibility = View.VISIBLE
                }
            }).start()
        }else{
            view.animate().alpha(0f).setDuration(1000).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    view.visibility = View.GONE
                }
            }).start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        
        mBinding = null
    }
}