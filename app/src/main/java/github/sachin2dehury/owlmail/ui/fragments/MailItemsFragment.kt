package github.sachin2dehury.owlmail.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import github.sachin2dehury.owlmail.NavGraphDirections
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.adapters.MailItemsAdapter
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.databinding.FragmentMailBoxBinding
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.others.Status
import github.sachin2dehury.owlmail.ui.ActivityExt
import github.sachin2dehury.owlmail.ui.viewmodels.MailItemsViewModel

//@AndroidEntryPoint
class MailItemsFragment : Fragment(R.layout.fragment_mail_box) {

    private var _binding: FragmentMailBoxBinding? = null
    private val binding: FragmentMailBoxBinding get() = _binding!!

    private val viewModel: MailItemsViewModel by viewModels()

    //    @Inject
    lateinit var mailItemsAdapter: MailItemsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMailBoxBinding.bind(view)

        setupAdapter()
        setupRecyclerView()
        subscribeToObservers()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.syncParsedMails()
        }

        binding.floatingActionButtonCompose.setOnClickListener {
            findNavController().navigate(NavGraphDirections.actionToComposeFragment())
        }

        (activity as ActivityExt).apply {
            toggleDrawer(true)
            toggleActionBar(true)
        }
    }

    private fun setupAdapter() = mailItemsAdapter.setOnItemClickListener {
        findNavController().navigate(NavGraphDirections.actionToMailItemFragment(it.id))
    }

    private fun setupRecyclerView() = binding.recyclerViewMailBox.apply {
        adapter = adapter
        layoutManager = LinearLayoutManager(requireContext())
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
                                (activity as ActivityExt).showSnackbar(message)
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
            }
        })
    }

    private fun setContent(result: Resource<List<Mail>>) {
        result.data?.let { mails ->
            mailItemsAdapter.mails = mails
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}