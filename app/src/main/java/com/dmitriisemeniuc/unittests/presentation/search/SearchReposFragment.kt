package com.dmitriisemeniuc.unittests.presentation.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dmitriisemeniuc.unittests.app.App
import com.dmitriisemeniuc.unittests.common.extensions.dpToPixel
import com.dmitriisemeniuc.unittests.common.extensions.isTrue
import com.dmitriisemeniuc.unittests.common.net.NetworkFailure
import com.dmitriisemeniuc.unittests.common.net.NotFoundException
import com.dmitriisemeniuc.unittests.common.net.Result
import com.dmitriisemeniuc.unittests.domain.model.Repo
import com.dmitriisemeniuc.unittests.presentation.extensions.hideKeyboard
import com.dmitriisemeniuc.unittests.presentation.extensions.isNetworkAvailable
import com.dmitriisemeniuc.unittests.presentation.extensions.showToast
import com.dmitriisemeniuc.unittests.presentation.search.adapter.ReposAdapter
import com.dmitriisemeniuc.unittests.presentation.search.viewmodel.SearchReposViewModel
import com.dmitriisemeniuc.unittests.presentation.search.viewmodel.SearchReposViewModelAssistedFactory
import com.dmitriisemeniuc.unittests.presentation.util.DefaultTextWatcher
import com.dmitriisemeniuc.unittests.presentation.util.ItemOffsetDecoration
import com.dmitriisemeniuc.unittests.R
import com.dmitriisemeniuc.unittests.databinding.FragmentSearchReposBinding
import retrofit2.HttpException
import javax.inject.Inject

class SearchReposFragment : Fragment() {

    private var _binding: FragmentSearchReposBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var assistedFactory: SearchReposViewModelAssistedFactory

    private val viewModel: SearchReposViewModel by viewModels {
        assistedFactory.create(this)
    }

    private lateinit var reposAdapter: ReposAdapter

    override fun onAttach(context: Context) {
        (context.applicationContext as App).appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchReposBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
        initReposRecyclerView()
        setSearchTextWatcher()
        observeSearchResult()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvRepos.adapter = null
        _binding = null
    }

    private fun setSearchTextWatcher() {
        binding.etSearch.addTextChangedListener(object : DefaultTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                enableSearchButton()
            }
        })
    }

    private fun initReposRecyclerView() {
        binding.rvRepos.apply {
            addItemDecoration(ItemOffsetDecoration(itemOffset = 6.dpToPixel()))
            reposAdapter = ReposAdapter { repo ->
                openRepo(repo)
            }
            adapter = reposAdapter
        }
    }

    private fun setListeners() {
        binding.btnSearch.setOnClickListener {
            activity?.hideKeyboard(binding.etSearch)
            val owner = binding.etSearch.text?.toString()?.trim()?.lowercase()
            owner?.let {
                viewModel.search(owner)
            }
        }
    }

    private fun observeSearchResult() {
        viewModel.repositories.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    onContentLoaded(result.data)
                }
                is Result.Loading -> {
                    onLoading()
                }
                is Result.Error -> {
                    onError(result.error)
                }
            }
        }
    }

    private fun onLoading() {
        clearAdapter()
        showLoadingView()
        disableSearchButton()
        binding.etSearch.clearFocus()
    }

    private fun onContentLoaded(data: List<Repo>) {
        hideLoadingView()
        if (data.isEmpty()) {
            showNoContentFound()
        } else {
            hideNoContentFound()
            showContent(data)
        }
    }

    private fun onError(error: Throwable) {
        enableSearchButton()
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

    private fun enableSearchButton() {
        binding.btnSearch.isEnabled = true
    }

    private fun disableSearchButton() {
        binding.btnSearch.isEnabled = false
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

    private fun showContent(data: List<Repo>) {
        reposAdapter.submitList(data)
    }

    private fun clearAdapter() {
        reposAdapter.submitList(null)
    }

    private fun openRepo(repo: Repo) {
        if (context?.isNetworkAvailable().isTrue()) {
            val action = SearchReposFragmentDirections.showRepo(
                owner = repo.user,
                repo = repo.name,
                repoId = repo.id
            )
            findNavController().navigate(action)
        } else {
            Toast.makeText(context, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT)
                .show()
        }
    }
}