package github.sachin2dehury.nitrmail.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import github.sachin2dehury.nitrmail.NavGraphDirections
import github.sachin2dehury.nitrmail.R
import github.sachin2dehury.nitrmail.adapters.MailBoxAdapter
import github.sachin2dehury.nitrmail.databinding.FragmentMailBoxBinding
import github.sachin2dehury.nitrmail.others.Status
import github.sachin2dehury.nitrmail.ui.ActivityExt
import github.sachin2dehury.nitrmail.ui.viewmodels.MailBoxViewModel
import javax.inject.Inject

abstract class MailBoxFragment : Fragment(R.layout.fragment_mail_box) {

    private var _binding: FragmentMailBoxBinding? = null
    private val binding: FragmentMailBoxBinding get() = _binding!!

    abstract val viewModel: MailBoxViewModel

    @Inject
    lateinit var mailBoxAdapter: MailBoxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.readLastSync()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMailBoxBinding.bind(view)

        setupAdapter()
        setupRecyclerView()
        subscribeToObservers()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.syncAllMails()
        }

        binding.floatingActionButtonCompose.setOnClickListener {
            findNavController().navigate(NavGraphDirections.actionToComposeFragment())
        }

        (activity as ActivityExt).apply {
            toggleDrawer(true)
            toggleActionBar(true)
        }
    }

    private fun setupAdapter() = mailBoxAdapter.setOnItemClickListener {
        findNavController().navigate(NavGraphDirections.actionToMailItemFragment(it.id))
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
                        binding.swipeRefreshLayout.isRefreshing = false
                        viewModel.saveLastSync()
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
        viewModel.searchQuery.observe(viewLifecycleOwner, { searchQuery ->
            searchQuery?.let {
                viewModel.syncSearchMails()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchAction = menu.findItem(R.id.searchBar).actionView
        val searchView = searchAction as SearchView
        searchView.apply {
            queryHint = "Search"
            isSubmitButtonEnabled = true
            setOnCloseListener {
                mailBoxAdapter.mails = mailBoxAdapter.list
                binding.swipeRefreshLayout.isRefreshing = false
                false
            }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    binding.swipeRefreshLayout.isRefreshing = true
                    viewModel.setSearchQuery(query)
                    return true
                }

                override fun onQueryTextChange(query: String): Boolean {
                    mailBoxAdapter.filter.filter(query)
                    return true
                }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}