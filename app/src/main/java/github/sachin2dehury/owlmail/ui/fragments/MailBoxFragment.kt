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
import github.sachin2dehury.owlmail.adapters.MailBoxAdapter
import github.sachin2dehury.owlmail.databinding.FragmentMailBoxBinding
import github.sachin2dehury.owlmail.others.ApiConstants
import github.sachin2dehury.owlmail.ui.viewmodels.MailBoxViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import javax.inject.Inject

@AndroidEntryPoint
class MailBoxFragment : Fragment(R.layout.fragment_mail_box) {

    private var _binding: FragmentMailBoxBinding? = null
    private val binding: FragmentMailBoxBinding get() = _binding!!

    private val viewModel: MailBoxViewModel by viewModels()

    private val args: MailBoxFragmentArgs by navArgs()

    @Inject
    lateinit var mailBoxAdapter: MailBoxAdapter

    @InternalCoroutinesApi
    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMailBoxBinding.bind(view)

        setupAdapter()
        setupRecyclerView()
        setContent()

        binding.swipeRefreshLayout.setOnRefreshListener {
            mailBoxAdapter.refresh()
        }

        binding.floatingActionButtonCompose.setOnClickListener {
            findNavController().navigate(
                NavGraphDirections.actionToComposeFragment(
                    ApiConstants.BASE_URL + ApiConstants.MOBILE_URL + ApiConstants.AUTH_FROM_COOKIE + ApiConstants.COMPOSE_MAIL
                )
            )
        }
    }

    private fun setupAdapter() = mailBoxAdapter.setupOnItemClickListener {
        findNavController().navigate(
            NavGraphDirections.actionToMailItemsFragment(it.conversationId, it.id)
        )
    }

    private fun setupRecyclerView() = binding.recyclerViewMailBox.apply {
        adapter = mailBoxAdapter
        layoutManager = LinearLayoutManager(context)
    }

    @InternalCoroutinesApi
    @ExperimentalPagingApi
    private fun setContent() {
        lifecycleScope.launchWhenCreated {
            mailBoxAdapter.loadStateFlow.collectLatest { loadStates ->
                binding.swipeRefreshLayout.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.getMails(args.request).collectLatest {
                mailBoxAdapter.submitData(it)
            }
        }
        lifecycleScope.launchWhenCreated {
            mailBoxAdapter.loadStateFlow
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