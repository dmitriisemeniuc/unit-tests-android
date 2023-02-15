package com.dmitriisemeniuc.unittests.data.source.local

import com.dmitriisemeniuc.unittests.data.db.entities.RepoEntity

interface ReposLocalDataSource {

    suspend fun getReposByUser(user: String): List<RepoEntity>
    suspend fun getRepoById(id: Long): RepoEntity?
    suspend fun saveRepo(repo: RepoEntity)
    suspend fun updateRepo(repo: RepoEntity)
    suspend fun saveRepos(repos: List<RepoEntity>)
}