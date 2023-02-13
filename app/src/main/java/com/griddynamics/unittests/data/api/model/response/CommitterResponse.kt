package com.griddynamics.unittests.data.api.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CommitterResponse(
    val login: String? = null,
    val date: String? = null
)