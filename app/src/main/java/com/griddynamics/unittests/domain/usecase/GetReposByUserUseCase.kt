package com.griddynamics.unittests.domain.usecase

import androidx.lifecycle.LiveData
import com.griddynamics.unittests.domain.model.Repo
import com.griddynamics.unittests.domain.repository.ReposRepository
import com.griddynamics.unittests.common.net.Result

class GetReposByUserUseCase(private val reposRepository: ReposRepository) {

    fun execute(user: String): LiveData<Result<List<Repo>>> {
        return reposRepository.getReposByUser(user)
    }
}