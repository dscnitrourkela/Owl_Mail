package github.sachin2dehury.nitrmail.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.adapters.MailBoxAdapter
import github.sachin2dehury.nitrmail.databinding.FragmentMailBoxBinding
import github.sachin2dehury.nitrmail.others.Constants
import github.sachin2dehury.nitrmail.others.InternetChecker
import github.sachin2dehury.nitrmail.others.Status
import github.sachin2dehury.nitrmail.ui.ActivityExt
import github.sachin2dehury.nitrmail.ui.viewmodels.MailBoxViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MailBoxFragment : Fragment(R.layout.fragment_mail_box) {

    private var _binding: FragmentMailBoxBinding? = null
    private val binding: FragmentMailBoxBinding get() = _binding!!

    lateinit var viewModel: MailBoxViewModel

    var lastSync = Constants.NO_LAST_SYNC

    @Inject
    lateinit var mailBoxAdapter: MailBoxAdapter

    @Inject
    lateinit var internetChecker: InternetChecker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as ActivityExt).apply {
            toggleDrawer(true)
            toggleActionBar(true)
        }

        _binding = FragmentMailBoxBinding.bind(view)

        viewModel = ViewModelProvider(requireActivity()).get(MailBoxViewModel::class.java)

        setupAdapter()
        setupRecyclerView()
        subscribeToObservers()
    }

    private fun setupAdapter() = mailBoxAdapter.setOnItemClickListener {
        findNavController().navigate(
            MailBoxFragmentDirections.actionMailBoxFragmentToMailItemFragment(
                it.id
            )
        )
    }

    private fun setupRecyclerView() = binding.recyclerViewMailBox.apply {
        adapter = mailBoxAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObservers() {
        viewModel.mails.observe(viewLifecycleOwner, {
            it?.let { event ->
                val result = event.peekContent()
                result.data?.let { mails ->
                    mailBoxAdapter.mails = mails
                }
                when (result.status) {
                    Status.SUCCESS -> {
                        if (internetChecker.isInternetConnected(requireContext())) {
                            viewModel.saveLastSync(lastSync)
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { message ->
                                (activity as ActivityExt).showSnackbar(message)
                            }
                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.LOADING -> {
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                }
            }
        })
        viewModel.request.observe(viewLifecycleOwner, { request ->
            request?.let {
                viewModel.readLastSync().invokeOnCompletion {
                    lastSync = System.currentTimeMillis()
                    viewModel.syncAllMails()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val search = menu.findItem(R.id.searchBar).actionView as SearchView
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mailBoxAdapter.filter.filter(newText)
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}