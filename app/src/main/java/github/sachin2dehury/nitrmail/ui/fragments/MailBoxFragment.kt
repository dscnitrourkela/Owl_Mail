package github.sachin2dehury.nitrmail.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.adapters.MailBoxAdapter
import github.sachin2dehury.nitrmail.databinding.FragmentMailBoxBinding
import github.sachin2dehury.nitrmail.others.InternetChecker
import github.sachin2dehury.nitrmail.others.Status
import github.sachin2dehury.nitrmail.services.SyncService
import github.sachin2dehury.nitrmail.ui.ActivityExt
import github.sachin2dehury.nitrmail.ui.viewmodels.MailBoxViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MailBoxFragment : Fragment(R.layout.fragment_mail_box) {

    private var _binding: FragmentMailBoxBinding? = null
    private val binding: FragmentMailBoxBinding get() = _binding!!

    private lateinit var viewModel: MailBoxViewModel

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

        _binding = FragmentMailBoxBinding.bind(view)

        viewModel = ViewModelProvider(requireActivity()).get(MailBoxViewModel::class.java)

        setupAdapter()
        setupRecyclerView()
        subscribeToObservers()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.syncAllMails()
        }

        (activity as ActivityExt).apply {
            toggleDrawer(true)
            toggleActionBar(true)
        }
    }

    private fun setupAdapter() = mailBoxAdapter.setOnItemClickListener {
        findNavController().navigate(
            MailBoxFragmentDirections.actionMailBoxFragmentToMailItemFragment(
                it.id, it.flag.contains('a')
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
                    mailBoxAdapter.list = mails
                    mailBoxAdapter.mails = mails
                }
                when (result.status) {
                    Status.SUCCESS -> {
                        if (internetChecker.isInternetConnected(requireContext())) {
                            viewModel.lastSync = System.currentTimeMillis() - 5000
                            viewModel.saveLastSync()
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
                    viewModel.syncAllMails()
                }
            }
        })
        viewModel.search.observe(viewLifecycleOwner, {
            it?.let { event ->
                val result = event.peekContent()
                result.data?.let { mails ->
                    mailBoxAdapter.mails = mails
                }
                when (result.status) {
                    Status.SUCCESS -> {
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logOut -> {
                viewModel.logOut()
                (requireActivity() as ActivityExt).apply {
//                    unregisterSync()
                    showSnackbar("Successfully logged out.")
                }
                binding.root.findNavController()
                    .navigate(R.id.action_mailBoxFragment_to_authFragment)
            }
            R.id.darkMode -> {
                val syncIntent = Intent(context, SyncService::class.java)
                requireContext().startService(syncIntent)
                (requireActivity() as ActivityExt).showSnackbar("Will be done. XD")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        inflater.inflate(R.menu.app_menu, menu)
        val searchAction = menu.findItem(R.id.searchBar).actionView
        val searchView = searchAction as SearchView
        searchView.queryHint = "Search"
        searchView.isSubmitButtonEnabled = true
        searchView.setOnCloseListener {
            mailBoxAdapter.mails = mailBoxAdapter.list
            binding.swipeRefreshLayout.isRefreshing = false
            false
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                binding.swipeRefreshLayout.isRefreshing = true
                viewModel.searchMails(query)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                mailBoxAdapter.filter.filter(query)
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