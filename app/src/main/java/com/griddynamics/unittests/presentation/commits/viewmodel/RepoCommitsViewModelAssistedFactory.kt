package com.griddynamics.unittests.presentation.commits.viewmodel

import androidx.savedstate.SavedStateRegistryOwner
import dagger.assisted.AssistedFactory

@AssistedFactory
interface RepoCommitsViewModelAssistedFactory {
    fun create(owner: SavedStateRegistryOwner): RepoCommitsViewModelFactory
}