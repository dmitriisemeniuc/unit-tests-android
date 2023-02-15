package com.dmitriisemeniuc.unittests.data.source.remote

import com.dmitriisemeniuc.unittests.data.api.GitHubApi
import com.dmitriisemeniuc.unittests.data.api.model.response.CommitsResponse

class CommitsRemoteDataSourceImpl(private val reposApi: GitHubApi) : CommitsRemoteDataSource {

    override suspend fun getCommitsByUserAndRepo(
        user: String,
        repo: String
    ): List<CommitsResponse> {
        return reposApi.getCommitsByUserAndRepo(user, repo)
    }
}