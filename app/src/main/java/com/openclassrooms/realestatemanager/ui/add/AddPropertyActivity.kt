package com.openclassrooms.realestatemanager.ui.add

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
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
import com.openclassrooms.realestatemanager.models.PointsOfInterest
import com.openclassrooms.realestatemanager.models.PropertyType
import com.openclassrooms.realestatemanager.ui.camera.CAMERA_RESULT_MEDIA_KEY
import com.openclassrooms.realestatemanager.ui.camera.CameraActivity
import com.openclassrooms.realestatemanager.ui.mediaViewer.BUNDLE_KEY_MEDIA_LIST
import com.openclassrooms.realestatemanager.ui.mediaViewer.BUNDLE_KEY_SELECTED_MEDIA_INDEX
import com.openclassrooms.realestatemanager.ui.mediaViewer.MediaViewerActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPropertyActivity : AppCompatActivity(), AddPropertyMediaListAdapter.MediaListener {
    private val mViewModel: AddActivityViewModel by viewModels()
    private lateinit var mBinding: ActivityAddPropertyBinding

    private val mAdapter = AddPropertyMediaListAdapter(this)

    override fun onCreate(savedInstancProperty: Bundle?) {
        super.onCreate(savedInstancProperty)

        mBinding = ActivityAddPropertyBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        configureUi()
        configureListeners()
        configureRecyclerView()

        setSupportActionBar(mBinding.activityAddPropertyToolbar)
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
        for (point in PointsOfInterest.values()) {
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
            if (mViewModel.pointOfInterestList.contains(point)) {
                chip.isChecked = true
            }
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    mViewModel.pointOfInterestList.add(buttonView.tag as PointsOfInterest)
                } else {
                    mViewModel.pointOfInterestList.remove(buttonView.tag as PointsOfInterest)
                }
            }
            mBinding.activityAddPropertyPointOfInterestCg.addView(chip)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun configureListeners() {
        mBinding.apply {
            activityAddPropertyAddMediaIb.setOnClickListener{ startCameraActivity() }
            activityAddPropertyTypeTvInput.onItemClickListener = dropdownListener()
            activityAddPropertyPriceEtInput.addTextChangedListener(priceTextWatcher())
            activityAddPropertySurfaceEtInput.addTextChangedListener(surfaceTextWatcher())
            activityAddPropertyRoomsEtInput.addTextChangedListener(roomsTextWatcher())
            activityAddPropertyBathroomsEtInput.addTextChangedListener(bathroomsTextWatcher())
            activityAddPropertyBedroomsEtInput.addTextChangedListener(bedroomsTextWatcher())
            activityAddPropertyDescriptionEtInput.addTextChangedListener(descriptionTextWatcher())
            activityAddPropertyAddressLine1EtInput.addTextChangedListener(address1TextWatcher())
            activityAddPropertyAddressLine2EtInput.addTextChangedListener(address2TextWatcher())
            activityAddPropertyAddressCityEtInput.addTextChangedListener(cityTextWatcher())
            activityAddPropertyAddressPostalCodeEtInput.addTextChangedListener(postalCodeTextWatcher())
            activityAddPropertyAddressCountryEtInput.addTextChangedListener(countryTextWatcher())
            activityAddPropertyAgentEtInput.addTextChangedListener(agentTextWatcher())
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
    }

    private fun dropdownListener() =
        AdapterView.OnItemClickListener { parent, _, position, _ ->
            if (parent != null) {
                mViewModel.propertyType = parent.getItemAtPosition(position) as PropertyType
                checkFields()
            }
        }

    private fun priceTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if(!s.isNullOrEmpty()) {
                try {
                    mViewModel.price = s.toString().toLong()
                } catch (e: Exception) {
                    mBinding.activityAddPropertyPriceEtInput.apply {
                        setText(mViewModel.price.toString())
                        setSelection(mViewModel.price.toString().length)
                    }
                }
            }
            checkFields()
        }
    }

    private fun surfaceTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if(!s.isNullOrEmpty()) {
                try {
                    mViewModel.surface = s.toString().toInt()
                } catch (e: Exception) {
                    mBinding.activityAddPropertySurfaceEtInput.apply {
                        setText(mViewModel.surface.toString())
                        setSelection(mViewModel.surface.toString().length)
                    }
                }
            }
            checkFields()
        }
    }

    private fun roomsTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if(!s.isNullOrEmpty()) {
                try {
                    mViewModel.rooms = s.toString().toInt()
                } catch (e : Exception){
                    mBinding.activityAddPropertyRoomsEtInput.apply {
                        setText(mViewModel.rooms.toString())
                        setSelection(mViewModel.rooms.toString().length)
                    }
                }
            }
            checkFields()
        }
    }

    private fun bathroomsTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if(!s.isNullOrEmpty()) {
                try {
                    mViewModel.bathrooms = s.toString().toInt()
                } catch (e: Exception) {
                    mBinding.activityAddPropertyBathroomsEtInput.apply {
                        setText(mViewModel.bathrooms.toString())
                        setSelection(mViewModel.bathrooms.toString().length)
                    }
                }
            }
            checkFields()
        }
    }

    private fun bedroomsTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if(!s.isNullOrEmpty()) {
                try {
                    mViewModel.bedrooms = s.toString().toInt()
                } catch (e: Exception) {
                    mBinding.activityAddPropertyBedroomsEtInput.apply {
                        setText(mViewModel.bedrooms.toString())
                        setSelection(mViewModel.bedrooms.toString().length)
                    }
                }
            }
            checkFields()
        }
    }

    private fun descriptionTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.description = s.toString()
            checkFields()
        }
    }

    private fun address1TextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.address1 = s.toString()
            checkFields()
        }
    }

    private fun address2TextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.address2 = s.toString()
        }
    }

    private fun cityTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.city = s.toString()
            checkFields()
        }
    }

    private fun postalCodeTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.postalCode = s.toString()
            checkFields()
        }
    }

    private fun countryTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.country = s.toString()
            checkFields()
        }
    }

    private fun agentTextWatcher() = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            mViewModel.agent = s.toString()
            checkFields()
        }
    }

    private fun saveProperty(){
            mViewModel.saveProperty()
            finish()
    }

    private fun checkFields(){
        mBinding.apply {
            val enableSaveBtn =
                !activityAddPropertyPriceEtInput.text.isNullOrEmpty() &&
                !activityAddPropertySurfaceEtInput.text.isNullOrEmpty() &&
                !activityAddPropertyRoomsEtInput.text.isNullOrEmpty() &&
                !activityAddPropertyBathroomsEtInput.text.isNullOrEmpty() &&
                !activityAddPropertyBedroomsEtInput.text.isNullOrEmpty() &&
                !activityAddPropertyAddressLine1EtInput.text.isNullOrEmpty() &&
                !activityAddPropertyAddressCityEtInput.text.isNullOrEmpty() &&
                !activityAddPropertyAddressPostalCodeEtInput.text.isNullOrEmpty() &&
                !activityAddPropertyAddressCountryEtInput.text.isNullOrEmpty() &&
                !activityAddPropertyAgentEtInput.text.isNullOrEmpty()
            activityAddPropertySaveBtn.isEnabled = enableSaveBtn
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        configureToolBar()
        return true
    }

    private fun configureToolBar(){
        mBinding.activityAddPropertyToolbar.title = resources.getString(R.string.toolbar_title_add_property)
        mBinding.activityAddPropertyToolbar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_back_arrow, null)
        mBinding.activityAddPropertyToolbar.setNavigationOnClickListener { finish() }
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
                    mViewModel.addMediaUri(mediaItem) // TODO Add media id
                }
            }
        }
    }

    override fun onMediaClick(position: Int) {
        val intent = Intent(this, MediaViewerActivity::class.java)
        intent.putParcelableArrayListExtra(BUNDLE_KEY_MEDIA_LIST, mViewModel.mediaList as ArrayList)
        intent.putExtra(BUNDLE_KEY_SELECTED_MEDIA_INDEX, position)
        startActivity(intent)
    }
}