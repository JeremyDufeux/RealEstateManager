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
import com.openclassrooms.realestatemanager.models.UserData
import com.openclassrooms.realestatemanager.models.enums.Currency
import com.openclassrooms.realestatemanager.models.enums.Unit
import com.openclassrooms.realestatemanager.utils.find
import com.openclassrooms.realestatemanager.utils.getStringResourceId
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private val mViewModel: SettingsViewModel by viewModels()

    private lateinit var mBinding: ActivitySettingsBinding

    private lateinit var mUnitAdapter: ArrayAdapter<String>
    private lateinit var mCurrencyAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        configureViewModel()
        configureUi()

        setSupportActionBar(mBinding.activitySettingsToolbar)
    }

    private fun configureViewModel() {
        mViewModel.userDataLiveData.observe(this, userDataObserver)
    }

    private val userDataObserver = Observer<UserData> { userData ->
        var index = mUnitAdapter.getPosition(getString(userData.unit.unitNameResId))
        mBinding.activitySettingsUnitTvInput.setText(mUnitAdapter.getItem(index).toString(), false)

        index = mCurrencyAdapter.getPosition(getString(userData.currency.currencyNameResId))
        mBinding.activitySettingsCurrencyTvInput.setText(mCurrencyAdapter.getItem(index).toString(), false)
    }


    private fun configureUi() {
        val unitArray = mutableListOf<String>()

        for (unit in Unit.values()){
            unitArray.add(getString(unit.unitNameResId))
        }

        mUnitAdapter = ArrayAdapter<String>(
            this,
            R.layout.list_item,
            unitArray
        )

        val currencyArray = mutableListOf<String>()

        for (currency in Currency.values()){
            currencyArray.add(getString(currency.currencyNameResId))
        }

        mBinding.activitySettingsUnitTvInput.apply {
            setAdapter(mUnitAdapter)
            setText(adapter.getItem(0).toString(), false)
            onItemClickListener = unitListener()
        }

        mCurrencyAdapter= ArrayAdapter<String>(
            this,
            R.layout.list_item,
            currencyArray
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
                val unit = Unit::unitNameResId.find(getStringResourceId(this, parent.getItemAtPosition(position) as String))!!
                mViewModel.userData.unit = unit
            }
        }

    private fun currencyListener() =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            if (parent != null) {
                val currency = Currency::currencyNameResId.find(getStringResourceId(this, parent.getItemAtPosition(position) as String))!!
                mViewModel.userData.currency = currency
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
            R.id.add_activity_save_settings_menu -> saveSettings()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveSettings() {
        mViewModel.saveSettings()
        finish()
    }
}