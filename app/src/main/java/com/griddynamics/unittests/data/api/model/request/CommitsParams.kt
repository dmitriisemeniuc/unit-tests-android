package com.griddynamics.unittests.data.api.model.request

data class CommitsParams(
    val user: String,
    val repo: String,
    val repoId: Long
)