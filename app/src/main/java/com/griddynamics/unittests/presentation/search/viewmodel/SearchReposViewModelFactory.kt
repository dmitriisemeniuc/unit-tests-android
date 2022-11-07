package com.griddynamics.unittests.presentation.search.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.griddynamics.unittests.domain.usecase.GetReposByUserUseCase

class SearchReposViewModelFactory(
    val application: Application,
    val getReposByUserUseCase: GetReposByUserUseCase) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchReposViewModel(getReposByUserUseCase = getReposByUserUseCase,
            application = application) as T
    }
}