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
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.models.State
import com.openclassrooms.realestatemanager.ui.details.BUNDLE_KEY_PROPERTY_ID
import com.openclassrooms.realestatemanager.ui.details.DetailsActivity
import com.openclassrooms.realestatemanager.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ListFragment : Fragment(), PropertyListAdapter.PropertyListener {
    private val mViewModel: ListViewModel by activityViewModels()
    private lateinit var mBinding : FragmentListBinding
    private val mAdapter = PropertyListAdapter(this)
    private lateinit var mPropertyList : List<Property>

    companion object {
        fun newInstance() = ListFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureViewModel()
    }

    private fun configureViewModel() {
        mViewModel.propertyListLiveData.observe(this, propertyListObserver)
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

    private val propertyListObserver = Observer<State<List<Property>>> { state ->
        when(state) {
            is State.Loading -> {
                mBinding.fragmentListPb.visibility = View.VISIBLE
                mBinding.fragmentListRv.visibility = View.GONE
            }
            is State.Success -> {
                mPropertyList = state.value
                mAdapter.updateList(mPropertyList)
                mBinding.fragmentListPb.visibility = View.GONE
                mBinding.fragmentListRv.visibility = View.VISIBLE
            }
            is State.Failure -> {
                mBinding.fragmentListPb.visibility = View.GONE
                showToast(requireContext(), R.string.an_error_append)
                Timber.e("Error ListFragment.propertyListObserver: ${state.throwable.toString()}")
            }
        }
    }

    override fun onPropertyClick(position: Int) {
        val intent = Intent(requireContext(), DetailsActivity::class.java)
        intent.putExtra(BUNDLE_KEY_PROPERTY_ID, mPropertyList[position].id)
        startActivity(intent)
    }

}