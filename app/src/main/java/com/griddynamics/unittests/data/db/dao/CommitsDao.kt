package com.griddynamics.unittests.data.db.dao

import androidx.room.*
import com.griddynamics.unittests.data.db.entities.CommitEntity

@Dao
interface CommitsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(commit: CommitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(commits: List<CommitEntity>)

    @Update
    suspend fun update(commit: CommitEntity): Int

    @Query("SELECT * FROM commits WHERE repoId = :repoId")
    suspend fun findByRepoId(repoId: Long): List<CommitEntity>?

    @Query("SELECT * FROM commits WHERE id = :id")
    suspend fun findById(id: Long): CommitEntity?
}