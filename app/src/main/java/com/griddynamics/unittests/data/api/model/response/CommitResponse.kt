package com.griddynamics.unittests.data.api.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CommitResponse(
    val message: String? = null,
    val committer: CommitterResponse? = null
)