package com.griddynamics.unittests.domain.model

data class Commit(
    val sha: String,
    val committer: String,
    val timestamp: Long,
    val message: String,
    val repoId: Long
)