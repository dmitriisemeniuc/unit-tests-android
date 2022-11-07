package com.griddynamics.unittests.domain.usecase

import com.griddynamics.unittests.domain.model.Repo
import com.griddynamics.unittests.domain.repository.ReposRepository
import com.griddynamics.unittests.common.net.Result
import kotlinx.coroutines.flow.Flow

class GetReposByUserUseCase(private val reposRepository: ReposRepository) {

    fun execute(user: String): Flow<Result<List<Repo>>> {
        return reposRepository.getReposByUser(user)
    }
}