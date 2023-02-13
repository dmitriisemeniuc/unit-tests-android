package com.griddynamics.unittests.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.griddynamics.unittests.data.db.dao.RepoCommitDao
import com.griddynamics.unittests.data.db.dao.ReposDao
import com.griddynamics.unittests.data.db.entities.RepoCommitEntity
import com.griddynamics.unittests.data.db.entities.ReposEntity

@Database(version = 1, entities = [ReposEntity::class, RepoCommitEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun getReposDao() : ReposDao

    abstract fun getRepoCommitDao() : RepoCommitDao

    companion object {
        const val DATABASE_NAME = "database-app"
    }
}