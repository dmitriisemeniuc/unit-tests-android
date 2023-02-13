package com.griddynamics.unittests.data.api

import com.griddynamics.unittests.data.api.model.response.RepoCommitsResponse
import com.griddynamics.unittests.data.api.model.response.RepoResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ReposApi {

    @GET(GET_REPOS_BY_USER_PATH)
    suspend fun getReposByUser(@Path("user") user: String?): List<RepoResponse>

    @GET(GET_REPO_COMMITS_BY_USER_AND_REPO_PATH)
    suspend fun getRepoCommitsByUserAndRepo(
        @Path("user") user: String?,
        @Path("repo") repo: String?
    ): List<RepoCommitsResponse>

    companion object {
        const val BASE_URL = "https://api.github.com"
        const val GET_REPOS_BY_USER_PATH = "/users/{user}/repos"
        const val GET_REPO_COMMITS_BY_USER_AND_REPO_PATH = "repos/{user}/{repo}/commits"
    }
}