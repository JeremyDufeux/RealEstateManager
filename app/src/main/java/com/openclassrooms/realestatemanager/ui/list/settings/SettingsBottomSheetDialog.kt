package com.openclassrooms.realestatemanager.ui.list.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.BottomSheetDialogSettingsBinding
import com.openclassrooms.realestatemanager.models.UserData
import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.enums.Unit
import com.openclassrooms.realestatemanager.utils.extensions.setCircleColor

class SettingsBottomSheetDialog : BottomSheetDialogFragment() {

    private val mViewModel: SettingsViewModel by activityViewModels()
    private var mBinding: BottomSheetDialogSettingsBinding? = null

    private lateinit var mContext: Context


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = BottomSheetDialogSettingsBinding.inflate(layoutInflater)
        mContext = mBinding!!.root.context

        configureViewModel()
        configureUi()

        return mBinding!!.root
    }

    private fun configureViewModel() {
        mViewModel.userDataLiveData.observe(this, userDataObserver)
    }

    private val userDataObserver = Observer<UserData> { userData ->
        mBinding?.settingsBottomUnitRg?.check(userData.unit.unitNameResId)
        mBinding?.settingsBottomCurrencyRg?.check(userData.currency.currencyNameResId)
    }


    private fun configureUi() {
        val unitArray = mutableListOf<String>()

        for (unit in Unit.values()){
            unitArray.add(getString(unit.unitNameResId))
        }

        mBinding?.settingsBottomSaveIb?.setOnClickListener { saveSettings() }

        for (unit in Unit.values()){
            val radioButton = RadioButton(mContext)
            radioButton.id = unit.unitNameResId
            radioButton.text = getString(unit.unitNameResId)
            radioButton.setCircleColor(ResourcesCompat.getColor(resources, R.color.colorAccent, null))
            mBinding?.settingsBottomUnitRg?.addView(radioButton)
        }
        mBinding?.settingsBottomUnitRg?.setOnCheckedChangeListener { _, checkedId ->
            mViewModel.userData.unit = Unit.values().find { it.unitNameResId == checkedId }!!
        }

        for (currency in Currency.values()){
            val radioButton = RadioButton(mContext)
            radioButton.id = currency.currencyNameResId
            radioButton.text = getString(currency.currencyNameResId)
            radioButton.setCircleColor(ResourcesCompat.getColor(resources, R.color.colorAccent, null))
            mBinding?.settingsBottomCurrencyRg?.addView(radioButton)
        }
        mBinding?.settingsBottomCurrencyRg?.setOnCheckedChangeListener { _, checkedId ->
            mViewModel.userData.currency = Currency.values().find { it.currencyNameResId == checkedId }!!
        }

    }

    private fun saveSettings() {
        mViewModel.saveSettings()
        dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()

        mBinding = null
    }
}