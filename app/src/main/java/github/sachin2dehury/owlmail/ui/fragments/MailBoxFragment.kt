package github.sachin2dehury.owlmail.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import github.sachin2dehury.owlmail.NavGraphDirections
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.adapters.MailBoxAdapter
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.databinding.FragmentMailBoxBinding
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.others.Status
import github.sachin2dehury.owlmail.ui.ActivityExt
import github.sachin2dehury.owlmail.ui.viewmodels.MailBoxViewModel
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
        viewModel.search.observe(viewLifecycleOwner, {
            it?.let { event ->
                val result = event.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        setSearchContent(result)
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { message ->
                                (activity as ActivityExt).showSnackbar(message)
                            }
                        }
                        setSearchContent(result)
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.LOADING -> {
                        setSearchContent(result)
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                }
            }
        })
        viewModel.mails.observe(viewLifecycleOwner, {
            it?.let { event ->
                val result = event.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        setContent(result)
                        binding.swipeRefreshLayout.isRefreshing = false
                        viewModel.saveLastSync()
                    }
                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { message ->
                                (activity as ActivityExt).showSnackbar(message)
                            }
                        }
                        setContent(result)
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.LOADING -> {
                        setContent(result)
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                }
            }
        })
    }

    private fun setContent(result: Resource<List<Mail>>) {
        result.data?.let { mails ->
            mailBoxAdapter.list = mails
            mailBoxAdapter.mails = mails
        }
    }

    private fun setSearchContent(result: Resource<List<Mail>>) {
        result.data?.let { mails ->
            mailBoxAdapter.mails = mails
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val searchAction = menu.findItem(R.id.searchBar).actionView
        val searchView = searchAction as SearchView
        (requireActivity() as ActivityExt).setSearchView(searchView)
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
                    return false
                }

                override fun onQueryTextChange(query: String): Boolean {
                    mailBoxAdapter.filter.filter(query)
                    return false
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