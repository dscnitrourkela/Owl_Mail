package github.sachin2dehury.owlmail.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import github.sachin2dehury.owlmail.R
import github.sachin2dehury.owlmail.adapters.MailItemsAdapter
import github.sachin2dehury.owlmail.api.data.Mail
import github.sachin2dehury.owlmail.databinding.FragmentMailItemsBinding
import github.sachin2dehury.owlmail.others.Resource
import github.sachin2dehury.owlmail.others.Status
import github.sachin2dehury.owlmail.ui.ActivityExt
import github.sachin2dehury.owlmail.ui.enableActionBar
import github.sachin2dehury.owlmail.ui.showSnackbar
import github.sachin2dehury.owlmail.ui.viewmodels.MailItemsViewModel
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

        (requireActivity() as AppCompatActivity).enableActionBar(true)
        (requireActivity() as ActivityExt).enableDrawer(false)
    }

    private fun setupRecyclerView() = binding.recyclerViewMailBox.apply {
        mailItemsAdapter.id = args.id
        adapter = mailItemsAdapter
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
            }
        })
    }

    private fun setContent(result: Resource<List<Mail>>) {
        result.data?.let { mails ->
            mailItemsAdapter.list = mails
            mailItemsAdapter.mails = mails
            binding.textViewMailSubject.text = mails.first().subject
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}