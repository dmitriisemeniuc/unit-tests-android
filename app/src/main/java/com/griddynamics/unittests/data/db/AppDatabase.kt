package com.griddynamics.unittests.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.griddynamics.unittests.data.db.dao.ReposDao
import com.griddynamics.unittests.data.db.entities.ReposEntity

@Database(version = 1, entities = [ReposEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun getReposDao() : ReposDao

    companion object {
        const val DATABASE_NAME = "database-app"
    }
}