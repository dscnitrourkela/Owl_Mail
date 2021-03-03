package github.sachin2dehury.owlmail.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.NavGraphDirections
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.adapters.MailItemsAdapter
import github.sachin2dehury.owlmail.databinding.FragmentMailItemsBinding
import github.sachin2dehury.owlmail.ui.viewmodels.MailItemsViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import javax.inject.Inject


@AndroidEntryPoint
class MailItemsFragment : Fragment(R.layout.fragment_mail_items) {

    private var _binding: FragmentMailItemsBinding? = null
    private val binding: FragmentMailItemsBinding get() = _binding!!

    private val viewModel: MailItemsViewModel by viewModels()

    private val args: MailItemsFragmentArgs by navArgs()

    @Inject
    lateinit var mailItemsAdapter: MailItemsAdapter

    @InternalCoroutinesApi
    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMailItemsBinding.bind(view)

        setupRecyclerView()
        setContent()

        binding.swipeRefreshLayout.setOnRefreshListener {
            mailItemsAdapter.refresh()
        }
    }

    private fun setupRecyclerView() = binding.recyclerViewMailBox.apply {
        mailItemsAdapter.id = args.id
        mailItemsAdapter.token = viewModel.getToken()
        mailItemsAdapter.css =
            requireContext().assets.open("Font").bufferedReader().use { it.readText() }
        mailItemsAdapter.setupOnItemClickListener { link ->
            findNavController().navigate(NavGraphDirections.actionToComposeFragment(link))
        }
        adapter = mailItemsAdapter
        layoutManager = LinearLayoutManager(context)
    }

    @InternalCoroutinesApi
    @ExperimentalPagingApi
    private fun setContent() {
        lifecycleScope.launchWhenCreated {
            mailItemsAdapter.loadStateFlow.collectLatest { loadStates ->
                binding.swipeRefreshLayout.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.getParsedMails(args.conversationId).collectLatest {
                mailItemsAdapter.submitData(it)
            }
        }
        lifecycleScope.launchWhenCreated {
            mailItemsAdapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collectLatest { binding.recyclerViewMailBox.scrollToPosition(0) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}