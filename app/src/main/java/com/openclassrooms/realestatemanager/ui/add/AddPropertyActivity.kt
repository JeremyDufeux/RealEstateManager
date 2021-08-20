package com.openclassrooms.realestatemanager.ui.add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityAddPropertyBinding
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.UserData
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import com.openclassrooms.realestatemanager.models.ui.PropertyUiAddView
import com.openclassrooms.realestatemanager.ui.camera.CAMERA_RESULT_MEDIA_KEY
import com.openclassrooms.realestatemanager.ui.camera.CameraActivity
import com.openclassrooms.realestatemanager.ui.mediaViewer.*
import com.openclassrooms.realestatemanager.utils.Utils.*
import com.openclassrooms.realestatemanager.utils.formatCalendarToString
import com.openclassrooms.realestatemanager.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPropertyActivity : AppCompatActivity(), AddPropertyMediaListAdapter.MediaListener {
    private val mViewModel: AddActivityViewModel by viewModels()
    private lateinit var mBinding: ActivityAddPropertyBinding

    private val mAdapter = AddPropertyMediaListAdapter(this)

    private lateinit var mTypeAdapter: ArrayAdapter<String>

    private val mChipList = mutableMapOf<PointOfInterest, Chip>()

    override fun onCreate(savedInstancProperty: Bundle?) {
        super.onCreate(savedInstancProperty)

        mBinding = ActivityAddPropertyBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        configureViewModel()
        configureUi()
        configureListeners()
        configureRecyclerView()

        setSupportActionBar(mBinding.activityAddPropertyToolbar)
    }

    private fun configureViewModel() {
        mViewModel.propertyLiveData.observe(this, propertyObserver)
        mViewModel.userDataLiveData.observe(this, userDataObserver)
    }

    private fun configureUi() {
        val typeList = mutableListOf<String>()
        for(poi in PropertyType.values()) {
            typeList.add(getString(poi.description))
        }

        mTypeAdapter= ArrayAdapter<String>(
            this,
            R.layout.list_item,
            typeList
        )

        // Property type dropdown list
        mBinding.activityAddPropertyTypeTvInput.apply {
            setAdapter(mTypeAdapter)
            setText(adapter.getItem(0).toString(), false)
        }

        // Points of interest chips
        for (point in PointOfInterest.values()) {
            val chip = layoutInflater.inflate(
                R.layout.single_chip_layout,
                mBinding.activityAddPropertyPointOfInterestCg,
                false
            ) as Chip
            chip.text = getString(point.description)
            chip.tag = point
            val image =
                ResourcesCompat.getDrawable(mBinding.root.context.resources, point.icon, null)
            chip.chipIcon = image
            chip.isCheckable = true
            if (mViewModel.mPointOfInterestList.contains(point)) {
                chip.isChecked = true
            }
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    mViewModel.mPointOfInterestList.add(buttonView.tag as PointOfInterest)
                } else {
                    mViewModel.mPointOfInterestList.remove(buttonView.tag as PointOfInterest)
                }
            }
            mChipList[chip.tag as PointOfInterest] = chip
            mBinding.activityAddPropertyPointOfInterestCg.addView(chip)
        }
    }

    private fun configureListeners() {
        mBinding.apply {
            activityAddPropertyAddMediaIb.setOnClickListener{ startCameraActivity() }
            activityAddPropertyTypeTvInput.onItemClickListener = dropdownListener()
            activityAddPropertySaveBtn.setOnClickListener { saveProperty() }
        }
    }

    private fun configureRecyclerView() {
        mBinding.activityAddPropertyMediasRv.adapter = mAdapter
        mBinding.activityAddPropertyMediasRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mViewModel.mediaListLiveData.observe(this, propertyMediaListObserver)
    }

    private val propertyMediaListObserver = Observer<List<MediaItem>> { list ->
        mAdapter.submitList(list)
        mAdapter.notifyDataSetChanged()
    }

    private val userDataObserver = Observer<UserData> { userData ->
        mBinding.activityAddPropertyPriceEt.suffixText = userData.currency.symbol
        mBinding.activityAddPropertySurfaceEt.suffixText = userData.unit.abbreviation
    }

    private fun dropdownListener() =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            if (parent != null) {
                mViewModel.propertyType = parent.getItemAtPosition(position) as PropertyType
            }
        }

    private val propertyObserver = Observer<PropertyUiAddView> { property ->
        mBinding.apply {
            val typeIndex = mTypeAdapter.getPosition(getString(property.type.description))
            mBinding.activityAddPropertyTypeTvInput.setText(mTypeAdapter.getItem(typeIndex).toString(), false)

            activityAddPropertyPriceEtInput.setText(property.priceString)
            activityAddPropertySurfaceEtInput.setText(property.surfaceString)
            activityAddPropertyRoomsEtInput.setText(property.roomsAmount)
            activityAddPropertyBathroomsEtInput.setText(property.bathroomsAmount)
            activityAddPropertyBedroomsEtInput.setText(property.bedroomsAmount)
            activityAddPropertyDescriptionEtInput.setText(property.description)
            activityAddPropertyAddressLine1EtInput.setText(property.addressLine1)
            activityAddPropertyAddressLine2EtInput.setText(property.addressLine2)
            activityAddPropertyAddressCityEtInput.setText(property.city)
            activityAddPropertyAddressPostalCodeEtInput.setText(property.postalCode)
            activityAddPropertyAddressCountryEtInput.setText(property.country)
            activityAddPropertyAgentEtInput.setText(property.agentName)

            activityAddPropertySoldDateEt.visibility = View.VISIBLE
            activityAddPropertySoldDateIv.visibility = View.VISIBLE

            activityAddPropertySoldDateEtInput.setText(property.soldDate?.let { formatCalendarToString(it) })
            activityAddPropertySoldDateEtInput.setOnClickListener { openDatePicker() }
            activityAddPropertySoldDateEt.isEndIconVisible = (property.soldDate != null)
            activityAddPropertySoldDateEt.setEndIconOnClickListener { clearDatePicker() }
        }

        for(poi in property.pointOfInterestList){
            mChipList[poi]?.isChecked = true
        }
    }

    private fun clearDatePicker() {
        mBinding.activityAddPropertySoldDateEtInput.setText("")
        mViewModel.soldDate = null
        mBinding.activityAddPropertySoldDateEt.isEndIconVisible = false
    }

    private fun openDatePicker() {
        val date = mViewModel.soldDate ?: MaterialDatePicker.todayInUtcMilliseconds()

        val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_sold_date))
                .setSelection(date)
                .setTheme(R.style.datePicker)
                .build()
        datePicker.show(supportFragmentManager, "DatePicker")
        datePicker.addOnPositiveButtonClickListener {
            mViewModel.soldDate = it
            mBinding.activityAddPropertySoldDateEtInput.setText(formatCalendarToString(it))
            mBinding.activityAddPropertySoldDateEt.isEndIconVisible = true
        }
    }

    private fun saveProperty(){
        if(mViewModel.mediaListLiveData.value != null && mViewModel.mediaListLiveData.value?.size!! >= 1) {
            loadDataToViewModel()
            mViewModel.saveProperty()
            finish()
        } else {
            showToast(this, R.string.please_add_a_picture_or_a_video)
        }
    }

    private fun loadDataToViewModel(){
        mBinding.apply {
            if (!activityAddPropertyPriceEtInput.text.isNullOrEmpty()) {
                try {
                    mViewModel.setPrice(activityAddPropertyPriceEtInput.text.toString())
                } catch (e: Exception) {
                    showToast(
                        this@AddPropertyActivity,
                        getString(
                            R.string.please_enter_between,
                            getString(R.string.price_amount),
                            Long.MAX_VALUE
                        )
                    )
                    return
                }
            }

            if (!activityAddPropertySurfaceEtInput.text.isNullOrEmpty()) {
                try {
                    mViewModel.setSurface(activityAddPropertySurfaceEtInput.text.toString())
                } catch (e: Exception) {
                    showToast(
                        this@AddPropertyActivity,
                        getString(
                            R.string.please_enter_between,
                            getString(R.string.surface_amount),
                            Int.MAX_VALUE
                        )
                    )
                    return
                }
            }

            if (!activityAddPropertyRoomsEtInput.text.isNullOrEmpty()) {
                try {
                    mViewModel.rooms = activityAddPropertyRoomsEtInput.text.toString().toInt()
                } catch (e: Exception) {
                    showToast(
                        this@AddPropertyActivity,
                        getString(
                            R.string.please_enter_between,
                            getString(R.string.room_amount),
                            Int.MAX_VALUE
                        )
                    )
                    return
                }
            }

            if (!activityAddPropertyBathroomsEtInput.text.isNullOrEmpty()) {
                try {
                    mViewModel.bathrooms =
                        activityAddPropertyBathroomsEtInput.text.toString().toInt()
                } catch (e: Exception) {
                    showToast(
                        this@AddPropertyActivity,
                        getString(
                            R.string.please_enter_between,
                            getString(R.string.bathroom_amount),
                            Int.MAX_VALUE
                        )
                    )
                    return
                }
            }

            if (!activityAddPropertyBedroomsEtInput.text.isNullOrEmpty()) {
                try {
                    mViewModel.bedrooms = activityAddPropertyBedroomsEtInput.text.toString().toInt()
                } catch (e: Exception) {
                    showToast(
                        this@AddPropertyActivity,
                        getString(
                            R.string.please_enter_between,
                            getString(R.string.bedroom_amount),
                            Int.MAX_VALUE
                        )
                    )
                    return
                }
            }

            mViewModel.description = activityAddPropertyDescriptionEtInput.text.toString()
            mViewModel.addressLine1 = activityAddPropertyAddressLine1EtInput.text.toString()
            mViewModel.addressLine2 = activityAddPropertyAddressLine2EtInput.text.toString()
            mViewModel.city = activityAddPropertyAddressCityEtInput.text.toString()
            mViewModel.postalCode = activityAddPropertyAddressPostalCodeEtInput.text.toString()
            mViewModel.country = activityAddPropertyAddressCountryEtInput.text.toString()
            mViewModel.agent = activityAddPropertyAgentEtInput.text.toString()
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
            R.id.add_activity_save_menu -> saveProperty()
        }
        return super.onOptionsItemSelected(item)
    }

    // ---------------
    // Camera activity
    // ---------------

    private fun startCameraActivity() {
        if (hasPermission()) {
            val intent = Intent(this, CameraActivity::class.java)
            resultLauncher.launch(intent)
        } else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

    private fun hasPermission() : Boolean{
        var perms = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,
            Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            perms = perms && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        }
        return perms
    }

    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if(hasPermission()){
            startCameraActivity()
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data

            if(data != null) {
                val mediaItem = data.getParcelableExtra<MediaItem>(CAMERA_RESULT_MEDIA_KEY)

                if (mediaItem != null) {
                    mViewModel.addMedia(mediaItem)
                }
            }
        }
    }

    private var openMediaViewerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data

            if(data != null) {
                val mediaItem = data.getParcelableExtra<MediaItem>(MEDIA_VIEWER_RESULT_MEDIA_KEY)
                if (mediaItem != null) {
                    mViewModel.updateMedia(mediaItem)
                }
            }
        }
    }

    override fun onMediaClick(position: Int) {
        val intent = Intent(this, MediaViewerActivity::class.java)
        intent.putParcelableArrayListExtra(BUNDLE_KEY_MEDIA_LIST, arrayListOf(mViewModel.mediaListLiveData.value?.get(position)))
        intent.putExtra(BUNDLE_KEY_SELECTED_MEDIA_INDEX, 0)
        intent.putExtra(BUNDLE_KEY_EDIT_MODE, true)
        openMediaViewerLauncher.launch(intent)
    }

    override fun onDeleteClick(position: Int) {
        mViewModel.removeMediaAtPosition(position)
    }
}