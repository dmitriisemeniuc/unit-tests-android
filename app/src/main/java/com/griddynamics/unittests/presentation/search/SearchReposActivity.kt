package com.griddynamics.unittests.presentation.search

import android.os.Bundle
import android.text.Editable
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.griddynamics.unittests.R
import com.griddynamics.unittests.app.App
import com.griddynamics.unittests.common.extensions.dpToPixel
import com.griddynamics.unittests.common.net.NetworkFailure
import com.griddynamics.unittests.common.net.NotFoundException
import com.griddynamics.unittests.common.net.Result
import com.griddynamics.unittests.databinding.ActivitySearchReposBinding
import com.griddynamics.unittests.domain.model.Repo
import com.griddynamics.unittests.presentation.extensions.hideKeyboard
import com.griddynamics.unittests.presentation.extensions.showToast
import com.griddynamics.unittests.presentation.search.adapter.ReposAdapter
import com.griddynamics.unittests.presentation.search.viewmodel.SearchReposViewModel
import com.griddynamics.unittests.presentation.search.viewmodel.SearchReposViewModelFactory
import com.griddynamics.unittests.presentation.util.DefaultTextWatcher
import com.griddynamics.unittests.presentation.util.ItemOffsetDecoration
import retrofit2.HttpException
import javax.inject.Inject

class SearchReposActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchReposBinding

    @Inject
    lateinit var viewModelFactory: SearchReposViewModelFactory
    private val viewModel: SearchReposViewModel by viewModels {
        viewModelFactory
    }

    private val reposAdapter by lazy { ReposAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchReposBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (applicationContext as App).appComponent.inject(this)

        setListeners()
        initReposRecyclerView()
        setSearchTextWatcher()
        observeSearchResult()
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
            adapter = reposAdapter
        }
    }

    private fun setListeners() {
        binding.btnSearch.setOnClickListener {
            hideKeyboard(binding.etSearch)
            val owner = binding.etSearch.text?.toString()?.trim()
            viewModel.search(owner)
        }
    }

    private fun observeSearchResult() {
        viewModel.repositories.observe(this) { result ->
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
                showToast(R.string.something_went_wrong)
            }
        }
    }

    private fun showNoInternetConnectionMessage() {
        showToast(R.string.no_internet_connection)
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
}