package com.openclassrooms.realestatemanager.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.ActivityDetailsBinding
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.ui.add.AddPropertyActivity
import com.openclassrooms.realestatemanager.utils.showToast
import com.openclassrooms.realestatemanager.utils.throwable.OfflineError
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

const val BUNDLE_KEY_PROPERTY_ID = "BUNDLE_KEY_PROPERTY_ID"

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {
    private val mViewModel: DetailsActivityViewModel by viewModels()

    lateinit var mBinding : ActivityDetailsBinding

    lateinit var propertyId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.activityDetailsToolbar)
        configureFragment()
        configureViewModel()
    }

    private fun configureFragment() {
        val detailFragment : DetailsFragment = supportFragmentManager.findFragmentById(R.id.activity_details_fragmentContainer) as DetailsFragment
        propertyId = intent?.extras?.getString(BUNDLE_KEY_PROPERTY_ID)!!

        detailFragment.setPropertyId(propertyId)
    }

    private fun configureViewModel() {
        mViewModel.stateLiveData.observe(this, stateObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_activity_toolbar_menu, menu)
        configureToolBar()
        return true
    }

    private fun configureToolBar(){
        mBinding.activityDetailsToolbar.title = resources.getString(R.string.toolbar_title_property_details)

        mBinding.activityDetailsToolbar.navigationIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_back_arrow, null)
        mBinding.activityDetailsToolbar.setNavigationOnClickListener { onBackPressed()}
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.edit_property -> openAddPropertyActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openAddPropertyActivity() {
        val intent = Intent(this, AddPropertyActivity::class.java)
        intent.putExtra(BUNDLE_KEY_PROPERTY_ID, propertyId)
        startActivity(intent)
    }

    private val stateObserver = Observer<State> { state ->
        when(state){
            is State.Upload.Uploading -> {
                mBinding.activityDetailsProgressLine.visibility = View.VISIBLE
            }
            is State.Upload.UploadSuccess.Empty -> {
                mBinding.activityDetailsProgressLine.visibility = View.GONE
                showToast(this, R.string.all_properties_has_been_uploaded)
            }
            is State.Upload.Error -> {
                mBinding.activityDetailsProgressLine.visibility = View.GONE
                if (state.throwable is OfflineError) {
                    showToast(this, R.string.the_property_will_be_uploaded_when_connected)
                } else {
                    showToast(this, R.string.an_error_append)
                }
                mViewModel.resetState()
                Timber.e("Error DetailsActivity.stateObserver: ${state.throwable.toString()}")
            }
            else -> {
                mBinding.activityDetailsProgressLine.visibility = View.GONE
            }
        }
    }
}