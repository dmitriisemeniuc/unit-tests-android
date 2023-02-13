package com.griddynamics.unittests.data.source.remote

import com.griddynamics.unittests.data.api.model.response.RepoResponse

interface ReposRemoteDataSource {

    suspend fun getReposByUser(user: String): List<RepoResponse>
}