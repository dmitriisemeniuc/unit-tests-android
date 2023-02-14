package com.griddynamics.unittests.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commits")
data class CommitEntity(
    @PrimaryKey
    val id: String,
    var repoId: Long,
    var committer: String,
    var timestamp: Long,
    var message: String
) {

    companion object {
        const val ID = "id"
        const val REPO_ID = 123L
        const val COMMITTER = "committer"
        const val TIMESTAMP = 1672531200L
        const val MESSAGE = "message"
    }
}