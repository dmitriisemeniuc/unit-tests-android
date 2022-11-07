package com.griddynamics.unittests.presentation.search.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.griddynamics.unittests.domain.usecase.GetReposByUserUseCase

class SearchReposViewModel(
    application: Application,
    private val getReposByUserUseCase: GetReposByUserUseCase
) : AndroidViewModel(application) {

    private val _owner = MutableLiveData<String>()
    private val owner: LiveData<String>
        get() = _owner

    val repositories = owner.switchMap { owner ->
        getReposByUserUseCase.execute(owner)
            .asLiveData(viewModelScope.coroutineContext)
    }

    fun search(owner: String?) {
        if(owner != null && owner.isNotEmpty()) {
            searchByOwner(owner)
        }
    }

    private fun searchByOwner(owner: String) {
        if (_owner.value != owner) {
            _owner.value = owner
        }
    }
}


