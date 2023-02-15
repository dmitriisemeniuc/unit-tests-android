package com.dmitriisemeniuc.unittests.domain.usecase

import androidx.lifecycle.LiveData
import com.dmitriisemeniuc.unittests.domain.model.Repo
import com.dmitriisemeniuc.unittests.domain.repository.ReposRepository
import com.dmitriisemeniuc.unittests.common.net.Result

class GetReposByUserUseCase(private val reposRepository: ReposRepository) {

    fun execute(user: String): LiveData<Result<List<Repo>>> {
        return reposRepository.getReposByUser(user)
    }
}