package com.griddynamics.unittests.domain.model

data class RepoCommit(
    val sha: String,
    val committer: String,
    val timestamp: Long,
    val message: String,
    val repoId: Long
)