package com.griddynamics.unittests.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commits")
data class RepoCommitEntity(
    @PrimaryKey
    val id: String,
    val repoId: Long,
    val committer: String,
    val timestamp: Long,
    val message: String
)