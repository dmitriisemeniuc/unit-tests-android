package com.dmitriisemeniuc.unittests.data.source.remote

import com.dmitriisemeniuc.unittests.data.api.model.response.RepoResponse

interface ReposRemoteDataSource {

    suspend fun getReposByUser(user: String): List<RepoResponse>
}