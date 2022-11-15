package com.griddynamics.unittests.data.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Owner(
    @SerialName("login")
    val login: String? = null,

    @SerialName("id")
    val id: Long? = null
)