package com.griddynamics.unittests.util

import com.griddynamics.unittests.data.api.model.Owner
import com.griddynamics.unittests.data.api.model.response.CommitResponse
import com.griddynamics.unittests.data.api.model.response.CommitsResponse
import com.griddynamics.unittests.data.api.model.response.CommitterResponse
import com.griddynamics.unittests.data.api.model.response.RepoResponse
import com.griddynamics.unittests.data.db.entities.CommitEntity
import com.griddynamics.unittests.data.db.entities.RepoEntity
import com.griddynamics.unittests.domain.model.Commit
import com.griddynamics.unittests.domain.model.Repo
import io.mockk.every
import io.mockk.mockk
import java.time.Instant

object TestUtil {

    fun generateReposApiResponse(user: String, count: Int = 10): List<RepoResponse> {
        val mockOwner = mockk<Owner> {
            every { id } returns 1
            every { login } returns user
        }
        return (0 until count + 1).map { i ->
            mockk {
                every { id } returns i.toLong()
                every { name } returns "Repo: ${i + 1}"
                every { description } returns "Description: ${i + 1}"
                every { fullName } returns mockOwner.login
                every { owner } returns mockOwner
            }
        }
    }

    fun generateRepoDomainModels(owner: String, count: Int = 10): List<Repo> {
        return (0 until count + 1).map { i ->
            Repo(
                id = i.toLong(),
                name = "Repo: ${i + 1}",
                description = "Description: ${i + 1}",
                user = owner
            )
        }
    }

    fun generateReposDbModels(owner: String, count: Int = 10): List<RepoEntity> {
        return (0 until count + 1).map { i ->
            RepoEntity(
                id = i.toLong(),
                name = "Repo: ${i + 1}",
                description = "Description: ${i + 1}",
                user = owner
            )
        }
    }

    fun generateCommitsApiResponse(user: String, count: Int = 10): List<CommitsResponse> {
        val dates = generateDates(count)
        return (0 until count + 1).map { i ->
            val mockedCommit = mockk<CommitResponse> {
                every { message } returns "Commit Message: ${i + 1}"
                every { committer } returns mockk {
                    every { date } returns dates[i]
                }
            }
            val mockedCommitter = mockk<CommitterResponse> {
                every { login } returns user
            }
            mockk {
                every { sha } returns i.toString()
                every { commit } returns mockedCommit
                every { committer } returns mockedCommitter
            }
        }
    }

    fun generateCommitDomainModels(committer: String, repoId: Long, count: Int = 10): List<Commit> {
        val dates = generateDates(count)
        return (0 until count + 1).map { i ->
            Commit(
                sha = i.toString(),
                message = "Commit Message: ${i + 1}",
                committer = committer,
                timestamp = dates[i].toTimeStamp(),
                repoId = repoId
            )
        }
    }

    fun generateCommitDbModels(user: String, repoId: Long, count: Int = 10): List<CommitEntity> {
        val dates = generateDates(count)
        return (0 until count + 1).map { i ->
            CommitEntity(
                id = i.toString(),
                repoId = repoId,
                message = "Commit Message: ${i + 1}",
                committer = user,
                timestamp = dates[i].toTimeStamp()
            )
        }
    }

    private fun generateDates(count: Int): List<String> {
        return (0 until count + 1).map { i ->
            val seconds = if (i < 10) "0$i" else "$i"
            "2020-01-01T10:00:${seconds}Z"
        }
    }

    private fun String.toTimeStamp(): Long {
        return Instant.parse(this).toEpochMilli()
    }
}