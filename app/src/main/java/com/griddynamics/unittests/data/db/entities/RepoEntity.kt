package com.griddynamics.unittests.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repos")
data class RepoEntity(
    @PrimaryKey
    val id: Long,
    var name: String,
    var description: String?,
    var user: String
) {
    companion object {
        const val ID = 1L
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val USER = "test_user"
    }
}