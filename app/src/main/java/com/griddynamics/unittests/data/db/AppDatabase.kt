package com.griddynamics.unittests.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.griddynamics.unittests.data.db.dao.CommitsDao
import com.griddynamics.unittests.data.db.dao.ReposDao
import com.griddynamics.unittests.data.db.entities.CommitEntity
import com.griddynamics.unittests.data.db.entities.RepoEntity

@Database(version = 1, entities = [RepoEntity::class, CommitEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun getReposDao() : ReposDao

    abstract fun getCommitsDao() : CommitsDao

    companion object {
        const val DATABASE_NAME = "database-app"
    }
}