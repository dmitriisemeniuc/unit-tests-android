package com.dmitriisemeniuc.unittests.domain.usecase

import androidx.lifecycle.LiveData
import com.dmitriisemeniuc.unittests.common.net.Result
import com.dmitriisemeniuc.unittests.data.api.model.request.CommitsParams
import com.dmitriisemeniuc.unittests.domain.model.Commit
import com.dmitriisemeniuc.unittests.domain.repository.CommitsRepository

class GetCommitsUseCase(private val commitsRepository: CommitsRepository) {

    fun execute(params: CommitsParams): LiveData<Result<List<Commit>>> {
        return commitsRepository.getCommitsByOwnerAndRepo(params)
    }
}