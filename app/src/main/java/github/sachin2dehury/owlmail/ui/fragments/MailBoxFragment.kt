package github.sachin2dehury.owlmail.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.NavGraphDirections
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.adapters.MailBoxAdapter
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.databinding.FragmentMailBoxBinding
import github.sachin2dehury.owlmail.others.Constants
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.others.Status
import github.sachin2dehury.owlmail.ui.showSnackbar
import github.sachin2dehury.owlmail.ui.viewmodels.MailBoxViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MailBoxFragment : Fragment(R.layout.fragment_mail_box) {

    private var _binding: FragmentMailBoxBinding? = null
    private val binding: FragmentMailBoxBinding get() = _binding!!

    private val viewModel: MailBoxViewModel by activityViewModels()

    private val args: MailBoxFragmentArgs by navArgs()

    @Inject
    lateinit var mailBoxAdapter: MailBoxAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMailBoxBinding.bind(view)

        viewModel.syncAllMails(args.request)
        setupAdapter()
        setupRecyclerView()
        subscribeToObservers()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.syncAllMails(args.request)
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

    private fun subscribeToObservers() {
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
                                view?.showSnackbar(message)
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

    private fun setContent(result: Resource<List<Mail>>) =
        result.data?.let { mails -> mailBoxAdapter.mails = mails }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}