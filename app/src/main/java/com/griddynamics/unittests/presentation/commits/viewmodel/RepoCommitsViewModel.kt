package com.griddynamics.unittests.presentation.commits.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.switchMap
import com.griddynamics.unittests.data.api.model.request.RepoCommitsParams
import com.griddynamics.unittests.domain.usecase.GetRepoCommitsUseCase

class RepoCommitsViewModel(
    application: Application,
    private val getRepoCommitsUseCase: GetRepoCommitsUseCase,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    val commits =
        savedStateHandle.getLiveData<Triple<String, String, Long>>(KEY_QUERY)
            .switchMap { (owner, repo, repoId) ->
                getRepoCommitsUseCase.execute(
                    RepoCommitsParams(
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