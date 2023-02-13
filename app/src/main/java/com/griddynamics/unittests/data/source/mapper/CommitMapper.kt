package com.griddynamics.unittests.data.source.mapper

import com.griddynamics.unittests.common.extensions.orZero
import com.griddynamics.unittests.data.api.model.response.CommitsResponse
import com.griddynamics.unittests.data.db.entities.CommitEntity
import com.griddynamics.unittests.domain.model.Commit
import java.time.Instant

class CommitMapper {

    fun mapDomainToStorage(commit: Commit): CommitEntity {
        return CommitEntity(
            id = commit.sha,
            committer = commit.committer,
            timestamp = commit.timestamp,
            message = commit.message,
            repoId = commit.repoId
        )
    }

    fun mapStorageToDomain(commit: CommitEntity): Commit {
        return Commit(
            sha = commit.id,
            repoId = commit.repoId,
            committer = commit.committer,
            timestamp = commit.timestamp,
            message = commit.message
        )
    }

    fun mapApiToDomain(response: CommitsResponse, repoId: Long): Commit {
        return Commit(
            sha = response.sha.orEmpty(),
            committer = response.committer?.login.orEmpty(),
            timestamp = Instant.parse(response.commit?.committer?.date).toEpochMilli().orZero(),
            message = response.commit?.message.orEmpty(),
            repoId = repoId
        )
    }
}