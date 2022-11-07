package com.griddynamics.unittests.presentation.search.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.griddynamics.unittests.domain.usecase.GetReposByUserUseCase

class SearchReposViewModel(
    application: Application,
    private val getReposByUserUseCase: GetReposByUserUseCase,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    val repositories = savedStateHandle.getLiveData<String>(KEY_OWNER).switchMap { owner ->
        getReposByUserUseCase.execute(owner)
            .asLiveData(viewModelScope.coroutineContext)
    }

    fun search(owner: String?) {
        if (owner != null && owner.isNotEmpty()) {
            searchByOwner(owner)
        }
    }

    private fun searchByOwner(owner: String) {
        val query = savedStateHandle.get<String>(KEY_OWNER)
        if (query != owner) {
            savedStateHandle[KEY_OWNER] = owner
        }
    }

    companion object {
        const val KEY_OWNER = "owner"
    }
}


