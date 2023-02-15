package com.dmitriisemeniuc.unittests.presentation.commits.viewmodel

import android.app.Application
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.dmitriisemeniuc.unittests.domain.usecase.GetCommitsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class CommitsViewModelFactory @AssistedInject constructor(
    val application: Application,
    private val getCommitsUseCase: GetCommitsUseCase,
    @Assisted owner: SavedStateRegistryOwner
) : AbstractSavedStateViewModelFactory(owner, null) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return CommitsViewModel(
            application = application,
            getCommitsUseCase = getCommitsUseCase,
            savedStateHandle = handle
        ) as T
    }
}