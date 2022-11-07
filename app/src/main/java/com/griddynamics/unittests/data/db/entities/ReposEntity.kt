package com.griddynamics.unittests.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repos")
data class ReposEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val description: String?,
    val user: String
)