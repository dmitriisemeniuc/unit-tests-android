package com.griddynamics.unittests.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.griddynamics.unittests.data.db.entities.ReposEntity

@Dao
interface ReposDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<ReposEntity>)

    @Query("SELECT * FROM repos WHERE user = :user")
    suspend fun findByUser(user: String): List<ReposEntity>?
}