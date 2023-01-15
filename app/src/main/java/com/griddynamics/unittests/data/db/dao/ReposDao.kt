package com.griddynamics.unittests.data.db.dao

import androidx.room.*
import com.griddynamics.unittests.data.db.entities.ReposEntity

@Dao
interface ReposDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(repo: ReposEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<ReposEntity>)

    @Update
    suspend fun update(repo: ReposEntity): Int

    @Query("SELECT * FROM repos WHERE user = :user")
    suspend fun findByUser(user: String): List<ReposEntity>?

    @Query("SELECT * FROM repos WHERE id = :id")
    suspend fun findById(id: Long): ReposEntity?
}