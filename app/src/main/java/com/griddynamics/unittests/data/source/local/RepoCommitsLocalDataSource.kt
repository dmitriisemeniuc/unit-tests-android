package com.griddynamics.unittests.data.source.local

import com.griddynamics.unittests.data.db.entities.RepoCommitEntity

interface RepoCommitsLocalDataSource {

    suspend fun getRepoCommitsByRepoId(repo: Long): List<RepoCommitEntity>
    suspend fun getRepoCommitById(id: Long): RepoCommitEntity?
    suspend fun saveRepoCommit(commit: RepoCommitEntity)
    suspend fun updateRepoCommit(commit: RepoCommitEntity)
    suspend fun saveRepoCommits(commits: List<RepoCommitEntity>)
}