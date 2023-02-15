package com.dmitriisemeniuc.unittests.data.source.local

import com.dmitriisemeniuc.unittests.data.db.entities.CommitEntity

interface CommitsLocalDataSource {

    suspend fun getCommitsByRepoId(repo: Long): List<CommitEntity>
    suspend fun getCommitById(id: String): CommitEntity?
    suspend fun saveCommit(commit: CommitEntity)
    suspend fun updateCommit(commit: CommitEntity)
    suspend fun saveCommits(commits: List<CommitEntity>)
}