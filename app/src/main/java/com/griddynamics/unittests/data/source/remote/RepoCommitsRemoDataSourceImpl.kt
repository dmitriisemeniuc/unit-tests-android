package com.griddynamics.unittests.data.source.remote

import com.griddynamics.unittests.data.api.ReposApi
import com.griddynamics.unittests.data.api.model.response.RepoCommitsResponse

class RepoCommitsRemoDataSourceImpl(private val reposApi: ReposApi) : RepoCommitsRemoteDataSource {

    override suspend fun getRepoCommitsByUserAndRepo(
        user: String,
        repo: String
    ): List<RepoCommitsResponse> {
        return reposApi.getRepoCommitsByUserAndRepo(user, repo)
    }
}