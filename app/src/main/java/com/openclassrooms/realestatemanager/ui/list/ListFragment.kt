package com.openclassrooms.realestatemanager.ui.list

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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment(), PropertyListAdapter.PropertyListener {
    private val mViewModel: ListViewModel by activityViewModels()
    private var mBinding : FragmentListBinding? = null
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

        return mBinding!!.root
    }

    private fun configureUi() {
        mBinding?.apply {
            fragmentListSrl.setOnRefreshListener { mViewModel.fetchProperties() }

            fragmentListRv.adapter = mAdapter
            fragmentListRv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            val itemDecoration: RecyclerView.ItemDecoration =
                DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            fragmentListRv.addItemDecoration(itemDecoration)
        }
    }

    private val propertiesObserver = Observer<List<PropertyUiListView>> { properties ->
        mPropertyList = properties
        mAdapter.updateList(properties)

        if(resources.getBoolean(R.bool.isTabletLand) && mPropertyList.isNotEmpty()) {
            if(mViewModel.selectedPropertyIdForTabletLan == null
                || mPropertyList.firstOrNull{ it.id == mViewModel.selectedPropertyIdForTabletLan} == null){
                mViewModel.selectedPropertyIdForTabletLan = properties[0].id
                mViewModel.setSelectedPropertyId(properties[0].id)
            } else {
                val index = mPropertyList.indexOfFirst { it.id == mViewModel.selectedPropertyIdForTabletLan }
                mAdapter.selectProperty(index)
            }
        }
    }

    private val stateObserver = Observer<State> { state ->
        when(state) {
            is State.Download.Downloading -> {
                mBinding?.fragmentListSrl?.isRefreshing = true
            }
            is State.Download.DownloadSuccess ->{
                hideProgress()
            }
            is State.Download.Error -> {
                hideProgress()
            }
            is State.Filter.Result -> {
                mBinding?.fragmentListSrl?.isEnabled = false
            }
            is State.Filter.Clear -> {
                mBinding?.fragmentListSrl?.isEnabled = true
            }
            else -> {
            }
        }
    }

    private fun hideProgress(){
        mBinding?.fragmentListSrl?.isRefreshing = false
    }

    override fun onPropertyClick(position: Int) {
        mViewModel.setSelectedPropertyId(mPropertyList[position].id)
    }

    override fun onDestroy() {
        super.onDestroy()

        mBinding = null
    }

}