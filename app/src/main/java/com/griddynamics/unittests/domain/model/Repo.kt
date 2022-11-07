package com.griddynamics.unittests.domain.model

data class Repo(
    val id: Long,
    val name: String,
    val description: String?,
    val user: String
)