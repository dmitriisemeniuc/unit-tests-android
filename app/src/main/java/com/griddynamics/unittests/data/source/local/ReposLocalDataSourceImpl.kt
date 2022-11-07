package com.griddynamics.unittests.data.source.local

import com.griddynamics.unittests.data.db.dao.ReposDao
import com.griddynamics.unittests.data.db.entities.ReposEntity

class ReposLocalDataSourceImpl(
    private val reposDao: ReposDao
) : ReposLocalDataSource {

    override suspend fun getReposByUser(user: String): List<ReposEntity> {
        return reposDao.findByUser(user).orEmpty()
    }

    override suspend fun saveRepos(repos: List<ReposEntity>) {
        reposDao.insertAll(repos)
    }
}