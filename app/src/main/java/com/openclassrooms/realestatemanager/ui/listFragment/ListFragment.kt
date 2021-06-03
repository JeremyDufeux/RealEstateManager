package com.openclassrooms.realestatemanager.ui.listFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.databinding.FragmentListBinding
import com.openclassrooms.realestatemanager.models.Estate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment(), EstateListAdapter.OnEstateListener{
    private val TAG = "ListFragment"

    private val listFragmentViewModel: ListFragmentViewModel by viewModels()
    private lateinit var mBinding : FragmentListBinding
    private val mAdapter = EstateListAdapter(this)

    companion object {
        fun newInstance() = ListFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureViewModel()
    }

    private fun configureViewModel() {
        listFragmentViewModel.observeEstateList().observe(this, estateListObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mBinding = FragmentListBinding.inflate(layoutInflater)

        configureRecyclerView()

        return mBinding.root
    }

    private fun configureRecyclerView() {
        mBinding.fragmentListRv.adapter = mAdapter
        mBinding.fragmentListRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val itemDecoration : RecyclerView.ItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        mBinding.fragmentListRv.addItemDecoration(itemDecoration)
    }

    private val estateListObserver = Observer<List<Estate>> {
        mAdapter.updateList(it)
    }

    override fun onEstateClick(position: Int) {
        Log.d(TAG, "onEstateClick: $position")
    }

}