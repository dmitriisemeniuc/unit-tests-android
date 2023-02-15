package com.dmitriisemeniuc.unittests.data.api.model

import kotlinx.serialization.Serializable

@Serializable
data class Owner(
    val login: String? = null,
    val id: Long? = null
)