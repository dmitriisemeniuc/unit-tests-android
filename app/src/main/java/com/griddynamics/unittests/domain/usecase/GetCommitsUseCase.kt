package com.griddynamics.unittests.domain.usecase

import androidx.lifecycle.LiveData
import com.griddynamics.unittests.common.net.Result
import com.griddynamics.unittests.data.api.model.request.CommitsParams
import com.griddynamics.unittests.domain.model.Commit
import com.griddynamics.unittests.domain.repository.CommitsRepository

class GetCommitsUseCase(private val commitsRepository: CommitsRepository) {

    fun execute(params: CommitsParams): LiveData<Result<List<Commit>>> {
        return commitsRepository.getCommitsByOwnerAndRepo(params)
    }
}