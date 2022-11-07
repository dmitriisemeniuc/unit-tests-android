package com.griddynamics.unittests.data.api.model

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
class Owner {
    @SerialName("login")
    val login: String? = null

    @SerialName("id")
    val id: Long? = null
}