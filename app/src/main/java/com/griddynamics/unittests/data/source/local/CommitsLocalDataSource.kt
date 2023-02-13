package com.griddynamics.unittests.data.source.local

import com.griddynamics.unittests.data.db.entities.CommitEntity

interface CommitsLocalDataSource {

    suspend fun getCommitsByRepoId(repo: Long): List<CommitEntity>
    suspend fun getCommitById(id: Long): CommitEntity?
    suspend fun saveCommit(commit: CommitEntity)
    suspend fun updateCommit(commit: CommitEntity)
    suspend fun saveCommits(commits: List<CommitEntity>)
}