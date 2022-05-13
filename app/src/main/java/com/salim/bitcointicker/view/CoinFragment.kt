package com.salim.bitcointicker.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.salim.bitcointicker.R
import com.salim.bitcointicker.adapter.CoinRecyclerAdapter
import com.salim.bitcointicker.adapter.PaginationScrollListener
import com.salim.bitcointicker.databinding.FragmentCoinBinding
import com.salim.bitcointicker.network.dto.coin.CoinItem
import com.salim.bitcointicker.utils.Status
import com.salim.bitcointicker.viewmodel.AuthViewModel
import com.salim.bitcointicker.viewmodel.CoinViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CoinFragment @Inject constructor(
    private val coinRecyclerAdapter: CoinRecyclerAdapter
): Fragment(R.layout.fragment_coin) {

    private var fragmentBinding: FragmentCoinBinding? = null
    lateinit var coinViewModel: CoinViewModel
    private val viewModel : AuthViewModel by activityViewModels()
    var list: ArrayList<CoinItem> = ArrayList()
    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getUser()
        listenToChannels()

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coinViewModel = ViewModelProvider(requireActivity())[CoinViewModel::class.java]

        val binding = FragmentCoinBinding.bind(view)
        fragmentBinding = binding

        binding.coinRecyclerView.adapter = coinRecyclerAdapter
        binding.coinRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        coinRecyclerAdapter.setOnItemClickListener {
            findNavController().navigate(CoinFragmentDirections.actionCoinFragmentToCoinDetailFragment())
        }

        binding.coinRecyclerView.addOnScrollListener(object : PaginationScrollListener(
            LinearLayoutManager(requireActivity())
        ) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                loadMoreItems()
                getMoreItems()
            }
        })

        binding.swipeToRefresh.setProgressBackgroundColorSchemeColor(Color.WHITE)
        binding.swipeToRefresh.setColorSchemeColors(Color.BLACK)
        binding.swipeToRefresh.setOnRefreshListener {
            observeUI()
            binding.swipeToRefresh.isRefreshing = false
        }

        coinViewModel.fetchCoinList()
        observeUI()
    }

    private fun observeUI() {
        coinViewModel.coinList.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    fragmentBinding?.progressBar?.visibility = View.GONE
                    val data = it.data!!
                    coinRecyclerAdapter.submitList(data)
                    fragmentBinding?.progressBar?.visibility = View.GONE
                }

                Status.ERROR -> {
                    fragmentBinding?.progressBar?.visibility = View.GONE
                    it.message?.let { message ->
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
                    }
                }
                Status.LOADING -> {
                    fragmentBinding?.progressBar?.visibility = View.VISIBLE
                }
            }
        }
    }

    fun getMoreItems() {
        isLoading = false
        coinRecyclerAdapter.addData(list)
    }

    private fun getUser() {
        viewModel.getCurrentUser()
    }

    private fun listenToChannels() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allEventsFlow.collect { event ->
                when(event){
                    is AuthViewModel.AllEvents.Message ->{
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
}




