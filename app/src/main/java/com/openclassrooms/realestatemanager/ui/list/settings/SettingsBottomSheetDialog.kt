package com.openclassrooms.realestatemanager.ui.list.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.BottomSheetDialogSettingsBinding
import com.openclassrooms.realestatemanager.models.UserData
import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.enums.Unit
import com.openclassrooms.realestatemanager.utils.find
import com.openclassrooms.realestatemanager.utils.getStringResourceId

class SettingsBottomSheetDialog : BottomSheetDialogFragment() {

    private val mViewModel: SettingsViewModel by viewModels()
    private var mBinding: BottomSheetDialogSettingsBinding? = null

    private lateinit var mContext: Context

    private lateinit var mUnitAdapter: ArrayAdapter<String>
    private lateinit var mCurrencyAdapter: ArrayAdapter<String>

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
        var index = mUnitAdapter.getPosition(getString(userData.unit.unitNameResId))
        mBinding?.settingsBottomUnitTvInput?.setText(mUnitAdapter.getItem(index).toString(), false)

        index = mCurrencyAdapter.getPosition(getString(userData.currency.currencyNameResId))
        mBinding?.settingsBottomCurrencyTvInput?.setText(mCurrencyAdapter.getItem(index).toString(), false)
    }


    private fun configureUi() {
        val unitArray = mutableListOf<String>()

        for (unit in Unit.values()){
            unitArray.add(getString(unit.unitNameResId))
        }

        mUnitAdapter = ArrayAdapter<String>(
            mContext,
            R.layout.list_item,
            unitArray
        )

        val currencyArray = mutableListOf<String>()

        for (currency in Currency.values()){
            currencyArray.add(getString(currency.currencyNameResId))
        }

        mBinding?.settingsBottomUnitTvInput?.apply {
            setAdapter(mUnitAdapter)
            setText(adapter.getItem(0).toString(), false)
            onItemClickListener = unitListener()
        }

        mCurrencyAdapter= ArrayAdapter<String>(
            mContext,
            R.layout.list_item,
            currencyArray
        )

        mBinding?.settingsBottomCurrencyTvInput?.apply {
            setAdapter(mCurrencyAdapter)
            setText(adapter.getItem(0).toString(), false)
            onItemClickListener = currencyListener()
        }

        mBinding?.settingsBottomSaveIb?.setOnClickListener { saveSettings() }
    }

    private fun unitListener() =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            if (parent != null) {
                val unit = Unit::unitNameResId.find(getStringResourceId(mContext, parent.getItemAtPosition(position) as String))!!
                mViewModel.userData.unit = unit
            }
        }

    private fun currencyListener() =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            if (parent != null) {
                val currency = Currency::currencyNameResId.find(getStringResourceId(mContext, parent.getItemAtPosition(position) as String))!!
                mViewModel.userData.currency = currency
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