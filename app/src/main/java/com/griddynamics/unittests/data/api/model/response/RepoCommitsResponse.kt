package com.griddynamics.unittests.data.api.model.response

import kotlinx.serialization.Serializable

@Serializable
data class RepoCommitsResponse(
    val sha: String? = null,
    val commit: RepoCommitResponse? = null,
    val committer: RepoCommitterResponse? = null
)