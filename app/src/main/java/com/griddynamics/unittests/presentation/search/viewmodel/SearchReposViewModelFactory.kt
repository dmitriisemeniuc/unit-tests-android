package com.griddynamics.unittests.presentation.search.viewmodel

import android.app.Application
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.griddynamics.unittests.domain.usecase.GetReposByUserUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class SearchReposViewModelFactory @AssistedInject constructor(
    val application: Application,
    private val getReposByUserUseCase: GetReposByUserUseCase,
    @Assisted owner: SavedStateRegistryOwner
) : AbstractSavedStateViewModelFactory(owner, null) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return SearchReposViewModel(
            application = application,
            getReposByUserUseCase = getReposByUserUseCase,
            savedStateHandle = handle
        ) as T
    }
}