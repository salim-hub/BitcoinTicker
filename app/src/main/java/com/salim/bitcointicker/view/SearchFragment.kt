package com.salim.bitcointicker.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.salim.bitcointicker.R
import com.salim.bitcointicker.adapter.SearchRecyclerAdapter
import com.salim.bitcointicker.databinding.FragmentSearchBinding
import com.salim.bitcointicker.utils.Status
import com.salim.bitcointicker.viewmodel.CoinViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment @Inject constructor(
    private val searchRecyclerAdapter: SearchRecyclerAdapter
) : Fragment(R.layout.fragment_search) {

    private lateinit var coinViewModel: CoinViewModel
    private var fragmentBinding: FragmentSearchBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchBinding.bind(view)
        fragmentBinding = binding

        initViewModel()
        initRecyclerView()
        subscribeToObservers()

        searchRecyclerAdapter.setOnItemClickListener {
            findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToCoinDetailFragment())
        }

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun initViewModel() {
        coinViewModel = ViewModelProvider(requireActivity())[CoinViewModel::class.java]

        coinViewModel.viewModelScope.launch {
            coinViewModel.getAllCoinList().observe(requireActivity()) {

                var job: Job? = null
                fragmentBinding?.searchEditText?.addTextChangedListener {
                    job?.cancel()
                    job = lifecycleScope.launch {
                        delay(500)
                        it?.let {
                            if (it.toString().isNotEmpty()) {
                                searchDatabase(it.toString())
                            }
                        }
                    }
                }
            }
            coinViewModel.makeApiCall()
        }
    }

    private fun initRecyclerView() {
        fragmentBinding?.searchRecyclerView?.adapter = searchRecyclerAdapter
        fragmentBinding?.searchRecyclerView?.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObservers() {
        coinViewModel.coinsList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val coins = it.data
                    searchRecyclerAdapter.coins = coins ?: listOf()
                    fragmentBinding?.progressBar?.visibility = View.GONE
                }

                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message ?: "Error", Toast.LENGTH_LONG)
                        .show()
                    fragmentBinding?.progressBar?.visibility = View.GONE
                }

                Status.LOADING -> {
                    fragmentBinding?.progressBar?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"

        coinViewModel.searchDatabase(searchQuery).observe(requireActivity()) { list ->
            list.let {
                searchRecyclerAdapter.submitList(it)
                searchRecyclerAdapter.notifyDataSetChanged()
            }
        }
    }
}