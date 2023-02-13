package com.griddynamics.unittests.data.source.remote

import com.griddynamics.unittests.data.api.GitHubApi
import com.griddynamics.unittests.data.api.model.response.RepoResponse

class ReposRemoteDataSourceImpl(private val reposApi: GitHubApi) : ReposRemoteDataSource {

    override suspend fun getReposByUser(user: String): List<RepoResponse> {
        return reposApi.getReposByUser(user)
    }
}