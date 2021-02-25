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
import github.sachin2dehury.owlmail.adapters.MailItemsAdapter
import github.sachin2dehury.owlmail.databinding.FragmentMailItemsBinding
import github.sachin2dehury.owlmail.ui.viewmodels.MailItemsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MailItemsFragment : Fragment(R.layout.fragment_mail_items) {

    private var _binding: FragmentMailItemsBinding? = null
    private val binding: FragmentMailItemsBinding get() = _binding!!

    private val viewModel: MailItemsViewModel by viewModels()

    private val args: MailItemsFragmentArgs by navArgs()

    @Inject
    lateinit var mailItemsAdapter: MailItemsAdapter

    @ExperimentalPagingApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMailItemsBinding.bind(view)

        setupRecyclerView()
        setContent()

        binding.swipeRefreshLayout.setOnRefreshListener {
            setContent()
        }
    }

    private fun setupRecyclerView() = binding.recyclerViewMailBox.apply {
        mailItemsAdapter.id = args.id
        mailItemsAdapter.token = viewModel.getToken()
        mailItemsAdapter.css =
            requireContext().assets.open("Font").bufferedReader().use { it.readText() }
        mailItemsAdapter.setupOnItemClickListener { part ->
            findNavController().navigate(
                NavGraphDirections.actionToComposeFragment(
                    "https://mail.nitrkl.ac.in/service/home/~/?id=${args.id}&part=$part"
                )
            )
        }
        adapter = mailItemsAdapter
        layoutManager = LinearLayoutManager(context)
    }

    @ExperimentalPagingApi
    private fun setContent() = lifecycleScope.launch {
        viewModel.getParsedMails(args.conversationId).collectLatest {
            mailItemsAdapter.submitData(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}