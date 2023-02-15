package com.dmitriisemeniuc.unittests.data.api.model.response

import com.dmitriisemeniuc.unittests.data.api.model.Owner
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoResponse(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    @SerialName("full_name")
    val fullName: String? = null,
    val owner: Owner? = null
)