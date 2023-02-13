package com.griddynamics.unittests.data.db.dao

import androidx.room.*
import com.griddynamics.unittests.data.db.entities.RepoCommitEntity

@Dao
interface RepoCommitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(commit: RepoCommitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(commits: List<RepoCommitEntity>)

    @Update
    suspend fun update(commit: RepoCommitEntity): Int

    @Query("SELECT * FROM commits WHERE repoId = :repoId")
    suspend fun findByRepoId(repoId: Long): List<RepoCommitEntity>?

    @Query("SELECT * FROM commits WHERE id = :id")
    suspend fun findById(id: Long): RepoCommitEntity?
}