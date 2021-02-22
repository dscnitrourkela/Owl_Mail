package github.sachin2dehury.owlmail.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.NavGraphDirections
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.adapters.MailItemsAdapter
import github.sachin2dehury.owlmail.api.data.ParsedMail
import github.sachin2dehury.owlmail.databinding.FragmentMailItemsBinding
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.others.Status
import github.sachin2dehury.owlmail.others.debugLog
import github.sachin2dehury.owlmail.ui.viewmodels.MailItemsViewModel
import github.sachin2dehury.owlmail.utils.showSnackbar
import kotlinx.coroutines.flow.first
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setConversationId(args.conversationId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMailItemsBinding.bind(view)

        setupRecyclerView()
        subscribeToObservers()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.setConversationId(args.conversationId)
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

    private fun subscribeToObservers() {
        viewModel.parsedMails.observe(viewLifecycleOwner, {
            it?.let { event ->
                val result = event.peekContent()
                when (result.status) {
                    Status.SUCCESS -> {
                        setContent(result)
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { errorResource ->
                            errorResource.message?.let { message ->
                                view?.showSnackbar(message)
                            }
                        }
                        setContent(result)
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    Status.LOADING -> {
                        setContent(result)
                        binding.recyclerViewMailBox.startLayoutAnimation()
                        binding.swipeRefreshLayout.isRefreshing = true
                    }
                }
                debugLog(mailItemsAdapter.quota ?: "")
            }
        })
    }

    private fun setContent(result: Resource<Pager<Int, ParsedMail>>) = lifecycleScope.launch {
        result.data?.flow?.cachedIn(lifecycleScope)?.first()
            ?.let { mailItemsAdapter.submitData(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}