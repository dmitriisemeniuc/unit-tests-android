package com.griddynamics.unittests.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repos")
data class ReposEntity(
    @PrimaryKey
    val id: Long,
    var name: String,
    var description: String?,
    var user: String
)