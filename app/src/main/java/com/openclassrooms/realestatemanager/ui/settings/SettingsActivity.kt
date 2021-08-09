package com.openclassrooms.realestatemanager.ui.settings

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivitySettingsBinding
import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.enums.Unit
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private val mViewModel: SettingsViewModel by viewModels()

    private lateinit var mBinding: ActivitySettingsBinding

    private lateinit var mUnitAdapter: ArrayAdapter<Unit>
    private lateinit var mCurrencyAdapter: ArrayAdapter<Currency>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        configureViewModel()
        configureUi()

        setSupportActionBar(mBinding.activitySettingsToolbar)
    }

    private fun configureViewModel() {
        mViewModel.unitLiveData.observe(this, unitObserver)
        mViewModel.currencyLiveData.observe(this, currencyObserver)
    }

    private val unitObserver = Observer<Unit> { unit ->
        val index = mUnitAdapter.getPosition(unit)
        mBinding.activitySettingsUnitTvInput.setText(mUnitAdapter.getItem(index).toString(), false)
    }

    private val currencyObserver = Observer<Currency> { currency ->
        val index = mCurrencyAdapter.getPosition(currency)
        mBinding.activitySettingsCurrencyTvInput.setText(mCurrencyAdapter.getItem(index).toString(), false)
    }

    private fun configureUi() {
        mUnitAdapter = ArrayAdapter<Unit>(
            this,
            R.layout.list_item,
            Unit.values()
        )

        mBinding.activitySettingsUnitTvInput.apply {
            setAdapter(mUnitAdapter)
            setText(adapter.getItem(0).toString(), false)
            onItemClickListener = unitListener()
        }

        mCurrencyAdapter= ArrayAdapter<Currency>(
            this,
            R.layout.list_item,
            Currency.values()
        )

        mBinding.activitySettingsCurrencyTvInput.apply {
            setAdapter(mCurrencyAdapter)
            setText(adapter.getItem(0).toString(), false)
            onItemClickListener = currencyListener()
        }

        mBinding.activitySettingsSaveBtn.setOnClickListener { saveSettings() }
    }

    private fun unitListener() =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            if (parent != null) {
                mViewModel.unit = parent.getItemAtPosition(position) as Unit
            }
        }

    private fun currencyListener() =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            if (parent != null) {
                mViewModel.currency = parent.getItemAtPosition(position) as Currency
            }
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_activity_toolbar_menu, menu)
        configureToolBar()
        return true
    }

    private fun configureToolBar(){
        mBinding.activitySettingsToolbar.title = resources.getString(R.string.toolbar_title_settings)
        mBinding.activitySettingsToolbar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_back_arrow, null)
        mBinding.activitySettingsToolbar.setNavigationOnClickListener { finish() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save_settings -> saveSettings()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveSettings() {
        mViewModel.saveSettings()
        finish()
    }
}