package com.griddynamics.unittests.data.source.remote

import com.griddynamics.unittests.data.api.model.response.CommitsResponse

interface CommitsRemoteDataSource {

    suspend fun getCommitsByUserAndRepo(user: String, repo: String): List<CommitsResponse>
}