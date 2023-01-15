package com.griddynamics.unittests.data.source.local

import com.griddynamics.unittests.data.db.entities.ReposEntity

interface  ReposLocalDataSource {

    suspend fun getReposByUser(user: String): List<ReposEntity>
    suspend fun getRepoById(id: Long): ReposEntity?
    suspend fun saveRepo(repo: ReposEntity)
    suspend fun updateRepo(repo: ReposEntity)
    suspend fun saveRepos(repos: List<ReposEntity>)
}