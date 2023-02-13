package com.griddynamics.unittests.util

import com.griddynamics.unittests.data.api.model.Owner
import com.griddynamics.unittests.data.api.model.response.RepoResponse
import com.griddynamics.unittests.data.db.entities.RepoEntity
import com.griddynamics.unittests.domain.model.Repo
import io.mockk.every
import io.mockk.mockk

object TestUtil {

    fun generateReposApiResponse(user: String, count: Int = 10) : List<RepoResponse> {
        val mockOwner = mockk<Owner> {
            every { id } returns 1
            every { login } returns user
        }
        return (1 until count + 1).map { i ->
            mockk {
                every { id } returns i.toLong()
                every { name } returns "Repo: $i"
                every { description } returns "Description: $i"
                every { fullName } returns mockOwner.login
                every { owner } returns mockOwner
            }
        }
    }
    fun generateRepoDomainModels(owner: String, count: Int = 10): List<Repo> {
        return (1 until count + 1).map { i ->
            Repo(
                id = i.toLong(),
                name = "Repo: $i",
                description = "Description: $i",
                user = owner
            )
        }
    }

    fun generateReposDbModels(owner: String, count: Int = 10): List<RepoEntity> {
        return (1 until count + 1).map { i ->
            RepoEntity(
                id = i.toLong(),
                name = "Repo: $i",
                description = "Description: $i",
                user = owner
            )
        }
    }
}