package com.openclassrooms.realestatemanager.ui.add

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityAddPropertyBinding
import com.openclassrooms.realestatemanager.models.MediaItem
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.enums.PointOfInterest
import com.openclassrooms.realestatemanager.models.enums.PropertyType
import com.openclassrooms.realestatemanager.ui.camera.CAMERA_RESULT_MEDIA_KEY
import com.openclassrooms.realestatemanager.ui.camera.CameraActivity
import com.openclassrooms.realestatemanager.ui.mediaViewer.*
import com.openclassrooms.realestatemanager.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPropertyActivity : AppCompatActivity(), AddPropertyMediaListAdapter.MediaListener {
    private val mViewModel: AddActivityViewModel by viewModels()
    private lateinit var mBinding: ActivityAddPropertyBinding

    private val mAdapter = AddPropertyMediaListAdapter(this)

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
    }

    private fun configureUi() {
        // Property type dropdown list
        val adapter: ArrayAdapter<PropertyType> = ArrayAdapter<PropertyType>(
            this,
            R.layout.list_item,
            PropertyType.values()
        )
        mBinding.activityAddPropertyTypeTvInput.apply {
            setAdapter(adapter)
            setText(adapter.getItem(0).toString(), false)
        }

        // Points of interest chips
        for (point in PointOfInterest.values()) {
            val chip = layoutInflater.inflate(
                R.layout.single_chip_layout,
                mBinding.activityAddPropertyPointOfInterestCg,
                false
            ) as Chip
            chip.text = point.description
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

    @SuppressLint("ClickableViewAccessibility")
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

    private fun dropdownListener() =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            if (parent != null) {
                mViewModel.propertyType = parent.getItemAtPosition(position) as PropertyType
            }
        }


    private val propertyObserver = Observer<Property> { property ->
        mBinding.apply {
            if(property.price != null) activityAddPropertyPriceEtInput.setText(property.price.toString())
            if(property.surface != null) activityAddPropertySurfaceEtInput.setText(property.surface.toString())
            if(property.roomsAmount != null) activityAddPropertyRoomsEtInput.setText(property.roomsAmount.toString())
            if(property.bathroomsAmount != null) activityAddPropertyBathroomsEtInput.setText(property.bathroomsAmount.toString())
            if(property.bedroomsAmount != null) activityAddPropertyBedroomsEtInput.setText(property.bedroomsAmount.toString())
            activityAddPropertyDescriptionEtInput.setText(property.description)
            activityAddPropertyAddressLine1EtInput.setText(property.addressLine1)
            activityAddPropertyAddressLine2EtInput.setText(property.addressLine2)
            activityAddPropertyAddressCityEtInput.setText(property.city)
            activityAddPropertyAddressPostalCodeEtInput.setText(property.postalCode)
            activityAddPropertyAddressCountryEtInput.setText(property.country)
            activityAddPropertyAgentEtInput.setText(property.agentName)
        }

        for(poi in property.pointOfInterestList){
            mChipList[poi]?.isChecked = true
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
                    mViewModel.price = activityAddPropertyPriceEtInput.text.toString().toLong()
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
                    mViewModel.surface = activityAddPropertySurfaceEtInput.text.toString().toInt()
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
            R.id.save_property -> saveProperty()
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
                    mViewModel.addMediaUri(mediaItem)
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


