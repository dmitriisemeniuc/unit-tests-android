package com.griddynamics.unittests.data.source.remote

import com.griddynamics.unittests.data.api.model.response.RepoCommitsResponse

interface RepoCommitsRemoteDataSource {

    suspend fun getRepoCommitsByUserAndRepo(user: String, repo: String): List<RepoCommitsResponse>
}