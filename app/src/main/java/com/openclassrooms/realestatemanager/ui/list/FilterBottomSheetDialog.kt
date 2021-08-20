package com.openclassrooms.realestatemanager.ui.list

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
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

    private val mViewModel: FilterViewModel by viewModels()
    private var mBinding: FilterBottomSheetDialogBinding? = null

    private var mApplyFiltersOnQuit = false

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
                mApplyFiltersOnQuit = true
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
                chip.isChecked = mViewModel.propertyTypeList.contains(it)

                filterBottomSheetTypeCg.addView(chip)

                chip.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        mViewModel.propertyTypeList.add(buttonView.tag as PropertyType)
                    } else {
                        mViewModel.propertyTypeList.remove(buttonView.tag as PropertyType)
                    }
                }
            }

            // City
            filterBottomSheetCityEtInput.setText(mViewModel.city)
            filterBottomSheetCityEt.isEndIconVisible = (mViewModel.city.isNotEmpty())
            filterBottomSheetCityEtInput.addTextChangedListener {
                mViewModel.city = it.toString()
            }

            // Price
            filterBottomSheetPriceRs.labelBehavior = LabelFormatter.LABEL_GONE
            filterBottomSheetPriceRs.valueFrom = mViewModel.minPrice
            filterBottomSheetPriceRs.valueTo = mViewModel.maxPrice
            filterBottomSheetPriceRs.setValues(mViewModel.minPrice, mViewModel.maxPrice)

            filterBottomSheetPriceRs.addOnChangeListener { slider, _, _ ->
                mViewModel.minPrice = slider.values[0]
                mViewModel.maxPrice = slider.values[1]
                filterBottomSheetPriceMinTv.text = mViewModel.minPrice.toInt().toString()
                filterBottomSheetPriceMaxTv.text = mViewModel.maxPrice.toInt().toString()
            }

            filterBottomSheetPriceMinTv.text = mViewModel.minPrice.toInt().toString()
            filterBottomSheetPriceMaxTv.text = mViewModel.maxPrice.toInt().toString()

            // Surface
            filterBottomSheetSurfaceRs.labelBehavior = LabelFormatter.LABEL_GONE
            filterBottomSheetSurfaceRs.valueFrom = mViewModel.minSurface
            filterBottomSheetSurfaceRs.valueTo = mViewModel.maxSurface
            filterBottomSheetSurfaceRs.setValues(mViewModel.minSurface, mViewModel.maxSurface)

            filterBottomSheetSurfaceRs.addOnChangeListener { slider, _, _ ->
                mViewModel.minSurface = slider.values[0]
                mViewModel.maxSurface = slider.values[1]
                filterBottomSheetSurfaceMinTv.text = mViewModel.minSurface.toInt().toString()
                filterBottomSheetSurfaceMaxTv.text = mViewModel.maxSurface.toInt().toString()
            }

            filterBottomSheetSurfaceMinTv.text = mViewModel.minSurface.toInt().toString()
            filterBottomSheetSurfaceMaxTv.text = mViewModel.maxSurface.toInt().toString()

            // Rooms
            filterBottomSheetRoomTv.text = mViewModel.roomAmount.toString()
            filterBottomSheetDecreaseRoomIb.isEnabled = (mViewModel.roomAmount != 0)
            filterBottomSheetDecreaseRoomIb.setOnClickListener {
                mViewModel.roomAmount--
                filterBottomSheetRoomTv.text = mViewModel.roomAmount.toString()
                filterBottomSheetDecreaseRoomIb.isEnabled = (mViewModel.roomAmount != 0)
            }
            filterBottomSheetIncreaseRoomIb.setOnClickListener {
                mViewModel.roomAmount++
                filterBottomSheetRoomTv.text = mViewModel.roomAmount.toString()
                filterBottomSheetDecreaseRoomIb.isEnabled = true
            }

            // Bathrooms
            filterBottomSheetBathroomTv.text = mViewModel.bathroomAmount.toString()
            filterBottomSheetDecreaseBathroomIb.isEnabled = (mViewModel.bathroomAmount != 0)
            filterBottomSheetDecreaseBathroomIb.setOnClickListener {
                mViewModel.bathroomAmount--
                filterBottomSheetBathroomTv.text = mViewModel.bathroomAmount.toString()
                filterBottomSheetDecreaseBathroomIb.isEnabled = (mViewModel.bathroomAmount != 0)
            }
            filterBottomSheetIncreaseBathroomIb.setOnClickListener {
                mViewModel.bathroomAmount++
                filterBottomSheetBathroomTv.text = mViewModel.bathroomAmount.toString()
                filterBottomSheetDecreaseBathroomIb.isEnabled = true
            }

            // Bedrooms
            filterBottomSheetBedroomTv.text = mViewModel.bedroomAmount.toString()
            filterBottomSheetDecreaseBedroomIb.isEnabled = (mViewModel.bedroomAmount != 0)
            filterBottomSheetDecreaseBedroomIb.setOnClickListener {
                mViewModel.bedroomAmount--
                filterBottomSheetBedroomTv.text = mViewModel.bedroomAmount.toString()
                filterBottomSheetDecreaseBedroomIb.isEnabled = (mViewModel.bedroomAmount != 0)
            }
            filterBottomSheetIncreaseBedroomIb.setOnClickListener {
                mViewModel.bedroomAmount++
                filterBottomSheetBedroomTv.text = mViewModel.bedroomAmount.toString()
                filterBottomSheetDecreaseBedroomIb.isEnabled = true
            }

            // Medias
            filterBottomSheetMediasTv.text = mViewModel.mediaAmount.toString()
            filterBottomSheetDecreaseMediasIb.isEnabled = (mViewModel.mediaAmount != 0)
            filterBottomSheetDecreaseMediasIb.setOnClickListener {
                mViewModel.mediaAmount--
                filterBottomSheetMediasTv.text = mViewModel.mediaAmount.toString()
                filterBottomSheetDecreaseMediasIb.isEnabled = (mViewModel.mediaAmount != 0)
            }
            filterBottomSheetIncreaseMediasIb.setOnClickListener {
                mViewModel.mediaAmount++
                filterBottomSheetMediasTv.text = mViewModel.mediaAmount.toString()
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
                chip.isChecked = mViewModel.pointOfInterestList.contains(it)

                filterBottomSheetPoiCg.addView(chip)

                chip.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        mViewModel.pointOfInterestList.add(buttonView.tag as PointOfInterest)
                    } else {
                        mViewModel.pointOfInterestList.remove(buttonView.tag as PointOfInterest)
                    }
                }
            }

            // Available check box
            filterBottomSheetAvailableCb.isChecked = mViewModel.available
            filterBottomSheetAvailableCb.setOnCheckedChangeListener { _, isChecked ->
                mViewModel.available = isChecked
                toggleView(filterBottomSheetPostDateEt, isChecked)

                if(isChecked && mViewModel.sold){
                    mViewModel.sold = false
                    filterBottomSheetSoldCb.isChecked = false
                }
            }

            // Post field
            if(mViewModel.postDate != null){
                filterBottomSheetPostDateEtInput.setText(formatCalendarToString(mViewModel.postDate!!))
            } else {
                filterBottomSheetPostDateEtInput.setText("")
            }
            filterBottomSheetPostDateEtInput.setOnClickListener { openPostDatePicker() }
            filterBottomSheetPostDateEt.setEndIconOnClickListener { clearPostDatePicker() }
            filterBottomSheetPostDateEt.isEndIconVisible = (mViewModel.soldDate != null)

            // Sold check box
            filterBottomSheetSoldCb.isChecked = mViewModel.sold
            filterBottomSheetSoldCb.setOnCheckedChangeListener { _, isChecked ->
                mViewModel.sold = isChecked
                toggleView(filterBottomSheetSoldDateEt, isChecked)

                if(isChecked && mViewModel.available){
                    mViewModel.available = false
                    filterBottomSheetAvailableCb.isChecked = false
                }
            }

            // Sold text view
            if(mViewModel.soldDate != null){
                filterBottomSheetSoldDateEtInput.setText(formatCalendarToString(mViewModel.soldDate!!))
            } else {
                filterBottomSheetSoldDateEtInput.setText("")
            }
            filterBottomSheetSoldDateEtInput.setOnClickListener { openSoldDatePicker() }
            filterBottomSheetSoldDateEt.setEndIconOnClickListener { clearSoldDatePicker() }
            filterBottomSheetSoldDateEt.isEndIconVisible = (mViewModel.soldDate != null)

            // Clear filter
            filterBottomSheetClearBtn.setOnClickListener { clearFilters() }
        }
    }

    private fun clearPostDatePicker() {
        mViewModel.soldDate = null
        mBinding?.apply {
            filterBottomSheetPostDateEtInput.setText("")
            filterBottomSheetPostDateEt.isEndIconVisible = false
        }
    }

    private fun openPostDatePicker() {
        val date = mViewModel.postDate ?: MaterialDatePicker.todayInUtcMilliseconds()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_estates_posted_since    ))
            .setSelection(date)
            .setTheme(R.style.datePicker)
            .build()
        datePicker.show(parentFragmentManager, "PostDatePicker")
        datePicker.addOnPositiveButtonClickListener {
            mViewModel.postDate = it

            mBinding?.apply {
                filterBottomSheetPostDateEtInput.setText(formatCalendarToString(it))
                filterBottomSheetPostDateEt.isEndIconVisible = true
            }
        }
    }

    private fun clearSoldDatePicker() {
        mViewModel.soldDate = null
        mBinding?.apply {
            filterBottomSheetSoldDateEtInput.setText("")
            filterBottomSheetSoldDateEt.isEndIconVisible = false
        }
    }

    private fun openSoldDatePicker() {
        val date = mViewModel.soldDate ?: MaterialDatePicker.todayInUtcMilliseconds()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_estates_sold_since))
            .setSelection(date)
            .setTheme(R.style.datePicker)
            .build()
        datePicker.show(parentFragmentManager, "SoldDatePicker")
        datePicker.addOnPositiveButtonClickListener {
            mViewModel.soldDate = it
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
            filterBottomSheetPriceRs.setValues(mViewModel.minPrice, mViewModel.maxPrice)
            filterBottomSheetSurfaceRs.setValues(mViewModel.minSurface, mViewModel.maxSurface)

            filterBottomSheetMediasTv.text = mViewModel.roomAmount.toString()
            filterBottomSheetRoomTv.text = mViewModel.roomAmount.toString()
            filterBottomSheetBathroomTv.text = mViewModel.bathroomAmount.toString()
            filterBottomSheetBedroomTv.text = mViewModel.bedroomAmount.toString()

            filterBottomSheetDecreaseMediasIb.isEnabled = false
            filterBottomSheetDecreaseRoomIb.isEnabled = false
            filterBottomSheetDecreaseBathroomIb.isEnabled = false
            filterBottomSheetDecreaseBedroomIb.isEnabled = false

            for (id in filterBottomSheetPoiCg.checkedChipIds){
                filterBottomSheetPoiCg.findViewById<Chip>(id).isChecked = false
            }

            filterBottomSheetCityEtInput.setText(mViewModel.city)
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