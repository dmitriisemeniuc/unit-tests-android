package com.griddynamics.unittests.data.api.model.response

import com.griddynamics.unittests.data.api.model.Owner
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoResponse(
    @SerialName("id")
    val id: Long? = null,

    @SerialName("name")
    val name: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("full_name")
    val fullName: String? = null,

    @SerialName("owner")
    val owner: Owner? = null
)