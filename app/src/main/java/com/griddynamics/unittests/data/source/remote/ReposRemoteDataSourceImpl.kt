package com.griddynamics.unittests.data.source.remote

import com.griddynamics.unittests.data.api.ReposApi
import com.griddynamics.unittests.data.api.model.RepoResponse

class ReposRemoteDataSourceImpl(private val reposApi: ReposApi) : ReposRemoteDataSource {

    override suspend fun getReposByUser(user: String): List<RepoResponse> {
        return reposApi.getReposByUser(user)
    }
}