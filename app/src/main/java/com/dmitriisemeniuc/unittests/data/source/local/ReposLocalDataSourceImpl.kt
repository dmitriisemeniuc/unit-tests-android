package com.dmitriisemeniuc.unittests.data.source.local

import com.dmitriisemeniuc.unittests.data.db.dao.ReposDao
import com.dmitriisemeniuc.unittests.data.db.entities.RepoEntity

class ReposLocalDataSourceImpl(
    private val reposDao: ReposDao
) : ReposLocalDataSource {

    override suspend fun getReposByUser(user: String): List<RepoEntity> {
        return reposDao.findByUser(user).orEmpty()
    }

    override suspend fun getRepoById(id: Long): RepoEntity? {
        return reposDao.findById(id)
    }

    override suspend fun saveRepos(repos: List<RepoEntity>) {
        reposDao.insertAll(repos)
    }

    override suspend fun saveRepo(repo: RepoEntity) {
        reposDao.insert(repo)
    }

    override suspend fun updateRepo(repo: RepoEntity) {
        reposDao.update(repo)
    }
}