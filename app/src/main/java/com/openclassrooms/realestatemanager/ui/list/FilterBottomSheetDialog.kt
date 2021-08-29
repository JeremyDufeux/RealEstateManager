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
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FilterBottomSheetDialogBinding
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import com.openclassrooms.realestatemanager.utils.formatCalendarToString

class FilterBottomSheetDialog: BottomSheetDialogFragment() {

    private val mViewModel: ListViewModel by activityViewModels()
    private var mBinding: FilterBottomSheetDialogBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = FilterBottomSheetDialogBinding.inflate(inflater)

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
            filterBottomSheetPriceMinEtInput.setText(mViewModel.propertyFilter.selectedMinPrice.toBigDecimal().toPlainString())
            filterBottomSheetPriceMinEtInput.addTextChangedListener( object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    if(s.toString().isNotEmpty()) {
                        try {
                            mViewModel.propertyFilter.selectedMinPrice = s.toString().toLong()
                        } catch (e: Exception){
                            mViewModel.propertyFilter.selectedMinPrice = mViewModel.propertyFilter.minPrice
                        }
                    } else {
                        mViewModel.propertyFilter.selectedMinPrice = mViewModel.propertyFilter.minPrice
                    }
                }
            })

            filterBottomSheetPriceMaxEtInput.setText(mViewModel.propertyFilter.selectedMaxPrice.toBigDecimal().toPlainString())
            filterBottomSheetPriceMaxEtInput.addTextChangedListener( object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    if(s.toString().isNotEmpty()) {
                        try {
                            mViewModel.propertyFilter.selectedMaxPrice = s.toString().toLong()
                        } catch (e: Exception){
                            mViewModel.propertyFilter.selectedMaxPrice = mViewModel.propertyFilter.maxPrice
                        }
                    } else {
                        mViewModel.propertyFilter.selectedMaxPrice = mViewModel.propertyFilter.maxPrice
                    }
                }
            })

            // Surface
            filterBottomSheetSurfaceMinEtInput.setText(mViewModel.propertyFilter.selectedMinSurface.toBigDecimal().toPlainString())
            filterBottomSheetSurfaceMinEtInput.addTextChangedListener( object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    if(s.toString().isNotEmpty()) {
                        try {
                            mViewModel.propertyFilter.selectedMinSurface = s.toString().toLong()
                        } catch (e: Exception){
                            mViewModel.propertyFilter.selectedMinSurface = mViewModel.propertyFilter.minSurface
                        }
                    } else {
                        mViewModel.propertyFilter.selectedMinSurface = mViewModel.propertyFilter.minSurface
                    }
                }
            })

            filterBottomSheetSurfaceMaxEtInput.setText(mViewModel.propertyFilter.selectedMaxSurface.toBigDecimal().toPlainString())
            filterBottomSheetSurfaceMaxEtInput.addTextChangedListener( object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    if(s.toString().isNotEmpty()) {
                        try {
                            mViewModel.propertyFilter.selectedMaxSurface = s.toString().toLong()
                        } catch (e: Exception){
                            mViewModel.propertyFilter.selectedMaxSurface = mViewModel.propertyFilter.maxSurface
                        }
                    } else {
                        mViewModel.propertyFilter.selectedMaxSurface = mViewModel.propertyFilter.maxSurface
                    }
                }
            })

            // Rooms
            filterBottomSheetRoomTv.text =mViewModel.propertyFilter.roomsAmount.toString()
            filterBottomSheetDecreaseRoomIb.isEnabled = (mViewModel.propertyFilter.roomsAmount != 0)
            filterBottomSheetDecreaseRoomIb.setOnClickListener {
               mViewModel.propertyFilter.roomsAmount--
                filterBottomSheetRoomTv.text =mViewModel.propertyFilter.roomsAmount.toString()
                filterBottomSheetDecreaseRoomIb.isEnabled = (mViewModel.propertyFilter.roomsAmount != 0)
            }
            filterBottomSheetIncreaseRoomIb.setOnClickListener {
               mViewModel.propertyFilter.roomsAmount++
                filterBottomSheetRoomTv.text =mViewModel.propertyFilter.roomsAmount.toString()
                filterBottomSheetDecreaseRoomIb.isEnabled = true
            }

            // Bathrooms
            filterBottomSheetBathroomTv.text =mViewModel.propertyFilter.bathroomsAmount.toString()
            filterBottomSheetDecreaseBathroomIb.isEnabled = (mViewModel.propertyFilter.bathroomsAmount != 0)
            filterBottomSheetDecreaseBathroomIb.setOnClickListener {
               mViewModel.propertyFilter.bathroomsAmount--
                filterBottomSheetBathroomTv.text =mViewModel.propertyFilter.bathroomsAmount.toString()
                filterBottomSheetDecreaseBathroomIb.isEnabled = (mViewModel.propertyFilter.bathroomsAmount != 0)
            }
            filterBottomSheetIncreaseBathroomIb.setOnClickListener {
               mViewModel.propertyFilter.bathroomsAmount++
                filterBottomSheetBathroomTv.text =mViewModel.propertyFilter.bathroomsAmount.toString()
                filterBottomSheetDecreaseBathroomIb.isEnabled = true
            }

            // Bedrooms
            filterBottomSheetBedroomTv.text =mViewModel.propertyFilter.bedroomsAmount.toString()
            filterBottomSheetDecreaseBedroomIb.isEnabled = (mViewModel.propertyFilter.bedroomsAmount != 0)
            filterBottomSheetDecreaseBedroomIb.setOnClickListener {
               mViewModel.propertyFilter.bedroomsAmount--
                filterBottomSheetBedroomTv.text =mViewModel.propertyFilter.bedroomsAmount.toString()
                filterBottomSheetDecreaseBedroomIb.isEnabled = (mViewModel.propertyFilter.bedroomsAmount != 0)
            }
            filterBottomSheetIncreaseBedroomIb.setOnClickListener {
               mViewModel.propertyFilter.bedroomsAmount++
                filterBottomSheetBedroomTv.text =mViewModel.propertyFilter.bedroomsAmount.toString()
                filterBottomSheetDecreaseBedroomIb.isEnabled = true
            }

            // Medias
            filterBottomSheetMediasTv.text =mViewModel.propertyFilter.mediasAmount.toString()
            filterBottomSheetDecreaseMediasIb.isEnabled = (mViewModel.propertyFilter.mediasAmount != 0)
            filterBottomSheetDecreaseMediasIb.setOnClickListener {
               mViewModel.propertyFilter.mediasAmount--
                filterBottomSheetMediasTv.text =mViewModel.propertyFilter.mediasAmount.toString()
                filterBottomSheetDecreaseMediasIb.isEnabled = (mViewModel.propertyFilter.mediasAmount != 0)
            }
            filterBottomSheetIncreaseMediasIb.setOnClickListener {
               mViewModel.propertyFilter.mediasAmount++
                filterBottomSheetMediasTv.text =mViewModel.propertyFilter.mediasAmount.toString()
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
                chip.isChecked =mViewModel.propertyFilter.pointOfInterestList.contains(it)

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
                }
            }

            // Post field
            if(mViewModel.propertyFilter.postDate != 0L){
                filterBottomSheetPostDateEtInput.setText(formatCalendarToString(mViewModel.propertyFilter.postDate))
            } else {
                filterBottomSheetPostDateEtInput.setText("")
            }
            filterBottomSheetPostDateEtInput.setOnClickListener { openPostDatePicker() }
            filterBottomSheetPostDateEt.setEndIconOnClickListener { clearPostDatePicker() }
            filterBottomSheetPostDateEt.isEndIconVisible = (mViewModel.propertyFilter.soldDate != 0L)

            // Sold check box
            filterBottomSheetSoldCb.isChecked =mViewModel.propertyFilter.sold
            filterBottomSheetSoldCb.setOnCheckedChangeListener { _, isChecked ->
               mViewModel.propertyFilter.sold = isChecked
                toggleView(filterBottomSheetSoldDateEt, isChecked)

                if(isChecked &&mViewModel.propertyFilter.available){
                   mViewModel.propertyFilter.available = false
                    filterBottomSheetAvailableCb.isChecked = false
                }
            }

            // Sold text view
            if(mViewModel.propertyFilter.soldDate != 0L){
                filterBottomSheetSoldDateEtInput.setText(formatCalendarToString(mViewModel.propertyFilter.soldDate))
            } else {
                filterBottomSheetSoldDateEtInput.setText("")
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
                filterBottomSheetPostDateEtInput.setText(formatCalendarToString(it))
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
                filterBottomSheetSoldDateEtInput.setText(formatCalendarToString(it))
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
            filterBottomSheetPriceMinEtInput.setText(mViewModel.propertyFilter.selectedMinPrice.toBigDecimal().toPlainString())
            filterBottomSheetPriceMaxEtInput.setText(mViewModel.propertyFilter.selectedMaxPrice.toBigDecimal().toPlainString())
            filterBottomSheetSurfaceMinEtInput.setText(mViewModel.propertyFilter.selectedMinSurface.toBigDecimal().toPlainString())
            filterBottomSheetSurfaceMaxEtInput.setText(mViewModel.propertyFilter.selectedMaxSurface.toBigDecimal().toPlainString())

            filterBottomSheetMediasTv.text =mViewModel.propertyFilter.roomsAmount.toString()
            filterBottomSheetRoomTv.text =mViewModel.propertyFilter.roomsAmount.toString()
            filterBottomSheetBathroomTv.text =mViewModel.propertyFilter.bathroomsAmount.toString()
            filterBottomSheetBedroomTv.text =mViewModel.propertyFilter.bedroomsAmount.toString()

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