package com.dmitriisemeniuc.unittests.data.db.dao

import androidx.room.*
import com.dmitriisemeniuc.unittests.data.db.entities.RepoEntity

@Dao
interface ReposDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(repo: RepoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<RepoEntity>)

    @Update
    suspend fun update(repo: RepoEntity): Int

    @Query("SELECT * FROM repos WHERE user = :user")
    suspend fun findByUser(user: String): List<RepoEntity>?

    @Query("SELECT * FROM repos WHERE id = :id")
    suspend fun findById(id: Long): RepoEntity?
}