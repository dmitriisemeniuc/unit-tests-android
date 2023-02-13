package com.griddynamics.unittests.domain.usecase

import androidx.lifecycle.LiveData
import com.griddynamics.unittests.common.net.Result
import com.griddynamics.unittests.data.api.model.request.RepoCommitsParams
import com.griddynamics.unittests.domain.model.RepoCommit
import com.griddynamics.unittests.domain.repository.RepoCommitsRepository

class GetRepoCommitsUseCase(private val repoCommitsRepository: RepoCommitsRepository) {

    fun execute(params: RepoCommitsParams): LiveData<Result<List<RepoCommit>>> {
        return repoCommitsRepository.getCommitsByOwnerAndRepo(params)
    }
}