package com.griddynamics.unittests.presentation.search.viewmodel

import androidx.savedstate.SavedStateRegistryOwner
import dagger.assisted.AssistedFactory

@AssistedFactory
interface SearchReposViewModelAssistedFactory {
    fun create(owner: SavedStateRegistryOwner): SearchReposViewModelFactory
}