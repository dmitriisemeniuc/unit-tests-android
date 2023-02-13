package com.griddynamics.unittests.data.api.model.response

import kotlinx.serialization.Serializable

@Serializable
data class RepoCommitResponse(
    val message: String? = null,
    val committer: RepoCommitterResponse? = null
)