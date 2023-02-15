package com.dmitriisemeniuc.unittests.presentation.commits.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.switchMap
import com.dmitriisemeniuc.unittests.data.api.model.request.CommitsParams
import com.dmitriisemeniuc.unittests.domain.usecase.GetCommitsUseCase

class CommitsViewModel(
    application: Application,
    private val getCommitsUseCase: GetCommitsUseCase,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    val commits =
        savedStateHandle.getLiveData<Triple<String, String, Long>>(KEY_QUERY)
            .switchMap { (owner, repo, repoId) ->
                getCommitsUseCase.execute(
                    CommitsParams(
                        user = owner,
                        repo = repo,
                        repoId = repoId
                    )
                )
            }

    fun findCommitsByOwnerAndRepo(owner: String, repo: String, repoId: Long) {
        savedStateHandle[KEY_QUERY] = Triple(owner, repo, repoId)
    }

    companion object {
        const val KEY_QUERY = "query"
    }
}