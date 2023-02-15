package com.dmitriisemeniuc.unittests.data.api

import com.dmitriisemeniuc.unittests.data.api.model.response.CommitsResponse
import com.dmitriisemeniuc.unittests.data.api.model.response.RepoResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApi {

    @GET(GET_REPOS_BY_USER_PATH)
    suspend fun getReposByUser(@Path("user") user: String?): List<RepoResponse>

    @GET(GET_COMMITS_BY_USER_AND_REPO_PATH)
    suspend fun getCommitsByUserAndRepo(
        @Path("user") user: String?,
        @Path("repo") repo: String?
    ): List<CommitsResponse>

    companion object {
        const val BASE_URL = "https://api.github.com"
        const val GET_REPOS_BY_USER_PATH = "/users/{user}/repos"
        const val GET_COMMITS_BY_USER_AND_REPO_PATH = "repos/{user}/{repo}/commits"
    }
}