package com.dmitriisemeniuc.unittests.data.source.local

import com.dmitriisemeniuc.unittests.data.db.dao.CommitsDao
import com.dmitriisemeniuc.unittests.data.db.entities.CommitEntity

class CommitsLocalDataSourceImpl(
    private val commitDao: CommitsDao
) : CommitsLocalDataSource {

    override suspend fun getCommitsByRepoId(repo: Long): List<CommitEntity> {
        return commitDao.findByRepoId(repo).orEmpty()
    }

    override suspend fun getCommitById(id: String): CommitEntity? {
        return commitDao.findById(id)
    }

    override suspend fun saveCommits(commits: List<CommitEntity>) {
        commitDao.insertAll(commits)
    }

    override suspend fun saveCommit(commit: CommitEntity) {
        commitDao.insert(commit)
    }

    override suspend fun updateCommit(commit: CommitEntity) {
        commitDao.update(commit)
    }
}