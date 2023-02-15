package com.dmitriisemeniuc.unittests.presentation.commits

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.dmitriisemeniuc.unittests.app.App
import com.dmitriisemeniuc.unittests.common.extensions.dpToPixel
import com.dmitriisemeniuc.unittests.common.net.NetworkFailure
import com.dmitriisemeniuc.unittests.common.net.NotFoundException
import com.dmitriisemeniuc.unittests.common.net.Result
import com.dmitriisemeniuc.unittests.domain.model.Commit
import com.dmitriisemeniuc.unittests.presentation.commits.adapter.RepoCommitAdapter
import com.dmitriisemeniuc.unittests.presentation.commits.viewmodel.CommitsViewModel
import com.dmitriisemeniuc.unittests.presentation.commits.viewmodel.CommitsViewModelAssistedFactory
import com.dmitriisemeniuc.unittests.presentation.extensions.showToast
import com.dmitriisemeniuc.unittests.presentation.util.ItemOffsetDecoration
import com.dmitriisemeniuc.unittests.R
import com.dmitriisemeniuc.unittests.databinding.FragmentCommitsBinding
import retrofit2.HttpException
import javax.inject.Inject

class CommitsFragment : Fragment() {

    private var _binding: FragmentCommitsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var assistedFactory: CommitsViewModelAssistedFactory

    private val viewModel: CommitsViewModel by viewModels {
        assistedFactory.create(this)
    }

    private lateinit var commitsAdapter: RepoCommitAdapter

    override fun onAttach(context: Context) {
        (context.applicationContext as App).appComponent.inject(this)
        super.onAttach(context)
    }

    private val params by navArgs<CommitsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initCommitsRecyclerView()
        observeCommits()
        viewModel.findCommitsByOwnerAndRepo(
            owner = params.owner,
            repo = params.repo,
            repoId = params.repoId
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvCommits.adapter = null
        _binding = null
    }

    private fun initCommitsRecyclerView() {
        binding.rvCommits.apply {
            addItemDecoration(ItemOffsetDecoration(itemOffset = 4.dpToPixel()))
            commitsAdapter = RepoCommitAdapter()
            adapter = commitsAdapter
        }
    }

    private fun observeCommits() {
        viewModel.commits.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    onContentLoaded(result.data)
                }
                is Result.Loading -> {
                    showLoadingView()
                }
                is Result.Error -> {
                    onError(result.error)
                }
            }
        }
    }

    private fun onContentLoaded(data: List<Commit>) {
        hideLoadingView()
        if (data.isEmpty()) {
            showNoContentFound()
        } else {
            hideNoContentFound()
            showContent(data)
        }
    }

    private fun showLoadingView() {
        binding.llLoading.isVisible = true
        binding.root.isEnabled = false
    }

    private fun hideLoadingView() {
        binding.llLoading.isVisible = false
        binding.root.isEnabled = true
    }

    private fun showNoContentFound() {
        binding.tvNoContentFound.isVisible = true
    }

    private fun hideNoContentFound() {
        binding.tvNoContentFound.isVisible = false
    }

    private fun showContent(data: List<Commit>) {
        commitsAdapter.submitList(data)
    }

    private fun onError(error: Throwable) {
        hideLoadingView()
        when (error) {
            is NetworkFailure -> {
                showNoInternetConnectionMessage()
            }
            is HttpException, is NotFoundException -> {
                showNoContentFound()
            }
            else -> {
                showNoContentFound()
                println("ERROR: ${error.message}")
                activity?.showToast(R.string.something_went_wrong)
            }
        }
    }

    private fun showNoInternetConnectionMessage() {
        activity?.showToast(R.string.no_internet_connection)
    }
}