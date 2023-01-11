package com.griddynamics.unittests.data.api

import com.griddynamics.unittests.data.api.model.RepoResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ReposApi {

    @GET(GET_REPOS_BY_USER_PATH)
    suspend fun getReposByUser(@Path("user") user: String?): List<RepoResponse>

    companion object {
        const val BASE_URL = "https://api.github.com"
        const val GET_REPOS_BY_USER_PATH = "/users/{user}/repos"
    }
}