package com.openclassrooms.realestatemanager.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.models.sealedClasses.State
import com.openclassrooms.realestatemanager.models.ui.PropertyUiListView
import com.openclassrooms.realestatemanager.ui.details.BUNDLE_KEY_PROPERTY_ID
import com.openclassrooms.realestatemanager.ui.details.DetailsActivity
import com.openclassrooms.realestatemanager.utils.showToast
import com.openclassrooms.realestatemanager.utils.throwable.OfflineError
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ListFragment : Fragment(), PropertyListAdapter.PropertyListener {
    private val mViewModel: ListViewModel by activityViewModels()
    private lateinit var mBinding : FragmentListBinding
    private val mAdapter = PropertyListAdapter(this)
    private lateinit var mPropertyList : List<PropertyUiListView>

    companion object {
        fun newInstance() = ListFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureViewModel()
    }

    private fun configureViewModel() {
        mViewModel.stateLiveData.observe(this, stateObserver)
        mViewModel.propertiesUiListViewLiveData.observe(this, propertiesObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mBinding = FragmentListBinding.inflate(layoutInflater)

        configureUi()

        return mBinding.root
    }

    private fun configureUi() {
        mBinding.fragmentListSrl.setOnRefreshListener { mViewModel.fetchProperties() }

        mBinding.fragmentListRv.adapter = mAdapter
        mBinding.fragmentListRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val itemDecoration : RecyclerView.ItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        mBinding.fragmentListRv.addItemDecoration(itemDecoration)
    }

    private val propertiesObserver = Observer<List<PropertyUiListView>> { properties ->
        mPropertyList = properties
        mAdapter.updateList(properties)
    }

    private val stateObserver = Observer<State> { state ->
        when(state) {
            is State.Download.Downloading -> {
                mBinding.fragmentListSrl.isRefreshing = true
            }
            is State.Download.DownloadSuccess ->{
                hideProgress()
            }
            is State.Download.Error -> {
                hideProgress()
                if (state.throwable is OfflineError) {
                    showToast(requireContext(), R.string.you_re_not_connected_to_internet)
                } else {
                    showToast(requireContext(), R.string.an_error_append)
                }
                Timber.e("Error ListFragment.stateObserver: ${state.throwable.toString()}")
            }
            else -> {
                hideProgress()
            }
        }
    }

    private fun hideProgress(){
        mBinding.fragmentListSrl.isRefreshing = false
        mBinding.fragmentListPb.visibility = View.GONE
    }

    override fun onPropertyClick(position: Int) {
        val intent = Intent(requireContext(), DetailsActivity::class.java)
        intent.putExtra(BUNDLE_KEY_PROPERTY_ID, mPropertyList[position].id)
        startActivity(intent)
    }

}