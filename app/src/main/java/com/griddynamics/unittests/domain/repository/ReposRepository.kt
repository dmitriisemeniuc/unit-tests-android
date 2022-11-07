package com.griddynamics.unittests.domain.repository

import com.griddynamics.unittests.common.net.Result
import com.griddynamics.unittests.domain.model.Repo
import kotlinx.coroutines.flow.Flow

interface ReposRepository {

    fun getReposByUser(user: String): Flow<Result<List<Repo>>>
}