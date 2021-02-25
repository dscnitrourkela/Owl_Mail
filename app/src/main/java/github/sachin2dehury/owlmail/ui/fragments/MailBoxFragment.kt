package github.sachin2dehury.owlmail.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.NavGraphDirections
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.adapters.MailBoxAdapter
import github.sachin2dehury.owlmail.databinding.FragmentMailBoxBinding
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.ui.viewmodels.MailBoxViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MailBoxFragment : Fragment(R.layout.fragment_mail_box) {

    private var _binding: FragmentMailBoxBinding? = null
    private val binding: FragmentMailBoxBinding get() = _binding!!

    private val viewModel: MailBoxViewModel by viewModels()

    private val args: MailBoxFragmentArgs by navArgs()

    @Inject
    lateinit var mailBoxAdapter: MailBoxAdapter

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
                    Constants.BASE_URL + Constants.MOBILE_URL + Constants.AUTH_FROM_COOKIE + Constants.COMPOSE_MAIL
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

    @ExperimentalPagingApi
    private fun setContent() = lifecycleScope.launch {
        viewModel.getMails(args.request).collectLatest {
            mailBoxAdapter.submitData(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}