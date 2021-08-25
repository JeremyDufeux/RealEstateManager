package com.openclassrooms.realestatemanager.ui.list

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.slider.LabelFormatter
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
                chip.isChecked =mViewModel.propertyFilter.propertyTypeList.contains(it)

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
            }

            // Price
            filterBottomSheetPriceRs.labelBehavior = LabelFormatter.LABEL_GONE
            filterBottomSheetPriceRs.valueFrom = mViewModel.propertyFilter.minPrice.toFloat()
            filterBottomSheetPriceRs.valueTo = mViewModel.propertyFilter.maxPrice.toFloat()
            filterBottomSheetPriceRs.setValues(mViewModel.propertyFilter.minPrice.toFloat(), mViewModel.propertyFilter.maxPrice.toFloat())

            filterBottomSheetPriceRs.addOnChangeListener { slider, _, _ ->
               mViewModel.propertyFilter.minPrice = slider.values[0].toDouble()
               mViewModel.propertyFilter.maxPrice = slider.values[1].toDouble()
                filterBottomSheetPriceMinTv.text =mViewModel.propertyFilter.minPrice.toInt().toString()
                filterBottomSheetPriceMaxTv.text =mViewModel.propertyFilter.maxPrice.toInt().toString()
            }

            filterBottomSheetPriceMinTv.text =mViewModel.propertyFilter.minPrice.toInt().toString()
            filterBottomSheetPriceMaxTv.text =mViewModel.propertyFilter.maxPrice.toInt().toString()

            // Surface
            filterBottomSheetSurfaceRs.labelBehavior = LabelFormatter.LABEL_GONE
            filterBottomSheetSurfaceRs.valueFrom =mViewModel.propertyFilter.minSurface.toFloat()
            filterBottomSheetSurfaceRs.valueTo =mViewModel.propertyFilter.maxSurface.toFloat()
            filterBottomSheetSurfaceRs.setValues(mViewModel.propertyFilter.minSurface.toFloat(), mViewModel.propertyFilter.maxSurface.toFloat())

            filterBottomSheetSurfaceRs.addOnChangeListener { slider, _, _ ->
               mViewModel.propertyFilter.minSurface = slider.values[0].toDouble()
               mViewModel.propertyFilter.maxSurface = slider.values[1].toDouble()
                filterBottomSheetSurfaceMinTv.text =mViewModel.propertyFilter.minSurface.toInt().toString()
                filterBottomSheetSurfaceMaxTv.text =mViewModel.propertyFilter.maxSurface.toInt().toString()
            }

            filterBottomSheetSurfaceMinTv.text =mViewModel.propertyFilter.minSurface.toInt().toString()
            filterBottomSheetSurfaceMaxTv.text =mViewModel.propertyFilter.maxSurface.toInt().toString()

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
            filterBottomSheetClearBtn.setOnClickListener { clearFilters() }
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

    private fun clearFilters() {
        mViewModel.resetValues()

        mBinding?.apply {
            for (id in filterBottomSheetTypeCg.checkedChipIds){
                filterBottomSheetTypeCg.findViewById<Chip>(id).isChecked = false
            }
            filterBottomSheetPriceRs.setValues(mViewModel.propertyFilter.minPrice.toFloat(), mViewModel.propertyFilter.maxPrice.toFloat())
            filterBottomSheetSurfaceRs.setValues(mViewModel.propertyFilter.minSurface.toFloat(), mViewModel.propertyFilter.maxSurface.toFloat())

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