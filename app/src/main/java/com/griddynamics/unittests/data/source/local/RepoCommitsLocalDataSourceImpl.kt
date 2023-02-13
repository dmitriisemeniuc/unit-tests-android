package com.griddynamics.unittests.data.source.local

import com.griddynamics.unittests.data.db.dao.RepoCommitDao
import com.griddynamics.unittests.data.db.entities.RepoCommitEntity

class RepoCommitsLocalDataSourceImpl(
    private val repoCommitDao: RepoCommitDao
) : RepoCommitsLocalDataSource {

    override suspend fun getRepoCommitsByRepoId(repo: Long): List<RepoCommitEntity> {
        return repoCommitDao.findByRepoId(repo).orEmpty()
    }

    override suspend fun getRepoCommitById(id: Long): RepoCommitEntity? {
        return repoCommitDao.findById(id)
    }

    override suspend fun saveRepoCommits(commits: List<RepoCommitEntity>) {
        repoCommitDao.insertAll(commits)
    }

    override suspend fun saveRepoCommit(commit: RepoCommitEntity) {
        repoCommitDao.insert(commit)
    }

    override suspend fun updateRepoCommit(commit: RepoCommitEntity) {
        repoCommitDao.update(commit)
    }
}