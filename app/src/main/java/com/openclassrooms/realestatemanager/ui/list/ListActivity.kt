package com.openclassrooms.realestatemanager.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.FirebaseFirestoreException.Code.DATA_LOSS
import com.google.firebase.firestore.FirebaseFirestoreException.Code.UNAVAILABLE
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityListBinding
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.ui.add.AddPropertyActivity
import com.openclassrooms.realestatemanager.ui.add.BUNDLE_KEY_ADD_ACTIVITY_PROPERTY_ID
import com.openclassrooms.realestatemanager.ui.details.BUNDLE_KEY_PROPERTY_ID
import com.openclassrooms.realestatemanager.ui.details.DetailsActivity
import com.openclassrooms.realestatemanager.ui.details.DetailsFragment
import com.openclassrooms.realestatemanager.ui.list.settings.SettingsBottomSheetDialog
import com.openclassrooms.realestatemanager.utils.showToast
import com.openclassrooms.realestatemanager.utils.throwable.OfflineError
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class ListActivity : AppCompatActivity() {
    private val mViewModel: ListViewModel by viewModels()

    private var mBinding : ActivityListBinding? = null

    private var editMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        mBinding = ActivityListBinding.inflate(layoutInflater)
        setContentView(mBinding?.root)

        configureViewModel()
        configureToolBar()
        configureViewPager()
        configureUi()
    }

    private fun configureViewModel(){
        mViewModel.stateLiveData.observe(this, stateObserver)
        mViewModel.selectedPropertyLiveData.observe(this, selectedPropertyObserver)
    }

    private fun configureToolBar() {
        setSupportActionBar(mBinding?.activityListToolbar)
    }

    private fun configureViewPager(){
        val pagerAdapter = ListActivityViewPagerAdapter(supportFragmentManager, lifecycle)
        pagerAdapter.addFragment(ListFragment.newInstance())
        pagerAdapter.addFragment(MapFragment.newInstance())

        mBinding?.activityListViewPager?.apply {
            adapter= pagerAdapter
            isUserInputEnabled = false
            offscreenPageLimit = 2
        }

        if(mBinding?.activityListTabLayout != null && mBinding?.activityListViewPager != null) {
            TabLayoutMediator(
                mBinding?.activityListTabLayout!!,
                mBinding?.activityListViewPager!!
            ) { tab, position ->
                when (position) {
                    0 -> tab.text = resources.getString(R.string.list_tab)
                    1 -> tab.text = resources.getString(R.string.map_tab)
                }
            }.attach()
        }
    }

    private fun configureUi(){
        mBinding?.activityListAddBtn?.setOnClickListener { openAddPropertyActivity() }
        mBinding?.activityListRemoveFilterBtn?.setOnClickListener { mViewModel.removeFilters() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_activity_toolbar_menu, menu)
        editMenuItem = menu?.findItem(R.id.list_activity_edit_menu)

        if(mViewModel.selectedPropertyLiveData.value != null){
            editMenuItem?.isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.list_activity_settings_menu -> showSettingsDialog()
            R.id.list_activity_edit_menu -> openEditPropertyActivity()
            R.id.list_activity_filter_menu -> showFilterDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFilterDialog() {
        val bottomDialog = FilterBottomSheetDialog()
        bottomDialog.show(supportFragmentManager, "FilterBottomSheetDialog")
    }

    private fun openAddPropertyActivity() {
        val addPropertyActivityIntent = Intent(this, AddPropertyActivity::class.java)
        startActivity(addPropertyActivityIntent)
    }

    private fun openEditPropertyActivity() {
        val addPropertyActivityIntent = Intent(this, AddPropertyActivity::class.java)
        addPropertyActivityIntent.putExtra(BUNDLE_KEY_ADD_ACTIVITY_PROPERTY_ID, mViewModel.selectedPropertyLiveData.value)
        startActivity(addPropertyActivityIntent)
    }

    private fun showSettingsDialog() {
        val settingsDialog = SettingsBottomSheetDialog()
        settingsDialog.show(supportFragmentManager, "SettingsBottomSheetDialog")
    }

    private val stateObserver = Observer<State> { state ->
        when(state){
            is State.Upload.Uploading -> {
                mBinding?.activityListProgressLine?.visibility = View.VISIBLE
            }
            is State.Upload.UploadSuccess.Empty -> {
                mBinding?.activityListProgressLine?.visibility = View.GONE
                showToast(this, R.string.all_properties_has_been_uploaded)
            }
            is State.Download.Error -> {
                when (state.throwable) {
                    is OfflineError -> {  showToast(this, R.string.you_re_not_connected_to_internet) }
                    is FirebaseFirestoreException -> {
                        when(state.throwable.code){
                            UNAVAILABLE, DATA_LOSS -> { showToast(this, R.string.you_re_not_connected_to_internet) }
                            else ->  {  showToast(this, R.string.an_error_append) }
                        }
                    }
                    else -> {  showToast(this, R.string.an_error_append) }
                }
                Timber.e("Error ListFragment.stateObserver: ${state.throwable.toString()}")
            }
            is State.Upload.Error -> {
                mBinding?.activityListProgressLine?.visibility = View.GONE
                if (state.throwable is OfflineError) {
                    showToast(this, R.string.the_property_will_be_uploaded_when_connected)
                } else {
                    showToast(this, R.string.an_error_append)
                }
                mViewModel.resetState()
                Timber.e("Error ListActivity.stateObserver: ${state.throwable.toString()}")
            }
            is State.Filter.Result -> {
                mBinding?.activityListRemoveFilterBtn?.visibility = View.VISIBLE
            }
            is State.Filter.Clear -> {
                mBinding?.activityListRemoveFilterBtn?.visibility = View.GONE
            }
            else -> {
                mBinding?.activityListProgressLine?.visibility = View.GONE
            }
        }
    }

    private val selectedPropertyObserver = Observer<String?> { propertyId ->
        if(propertyId != null) {
            mViewModel.selectedPropertyIdForTabletLan = propertyId
            if (resources.getBoolean(R.bool.isTabletLand)) {
                showDetailsFragmentForTablet()
            } else {
                val detailsActivityIntent = Intent(this, DetailsActivity::class.java)
                detailsActivityIntent.putExtra(BUNDLE_KEY_PROPERTY_ID, propertyId)
                startActivity(detailsActivityIntent)
            }
        }
    }

    private fun showDetailsFragmentForTablet(){
        editMenuItem?.isVisible = true

        mBinding?.apply {
            activityListIconIv?.visibility = View.INVISIBLE
            activityListDetailsFragment?.visibility = View.VISIBLE
        }
        val detailFragment: DetailsFragment =
            supportFragmentManager.findFragmentById(R.id.activity_list_details_fragment) as DetailsFragment
        mViewModel.selectedPropertyIdForTabletLan?.let { detailFragment.setPropertyId(it) }
    }

    override fun onDestroy() {
        super.onDestroy()

        mBinding = null
    }
}