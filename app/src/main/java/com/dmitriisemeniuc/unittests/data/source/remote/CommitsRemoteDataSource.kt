package com.dmitriisemeniuc.unittests.data.source.remote

import com.dmitriisemeniuc.unittests.data.api.model.response.CommitsResponse

interface CommitsRemoteDataSource {

    suspend fun getCommitsByUserAndRepo(user: String, repo: String): List<CommitsResponse>
}