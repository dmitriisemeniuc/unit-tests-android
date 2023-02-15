package com.dmitriisemeniuc.unittests.data.api.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CommitsResponse(
    val sha: String? = null,
    val commit: CommitResponse? = null,
    val committer: CommitterResponse? = null
)