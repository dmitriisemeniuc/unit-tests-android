package com.griddynamics.unittests.presentation.commits.viewmodel

import androidx.savedstate.SavedStateRegistryOwner
import dagger.assisted.AssistedFactory

@AssistedFactory
interface CommitsViewModelAssistedFactory {
    fun create(owner: SavedStateRegistryOwner): CommitsViewModelFactory
}