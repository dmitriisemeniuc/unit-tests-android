package com.griddynamics.unittests.data.source.mapper

import com.griddynamics.unittests.common.extensions.orZero
import com.griddynamics.unittests.data.api.model.response.RepoCommitsResponse
import com.griddynamics.unittests.data.db.entities.RepoCommitEntity
import com.griddynamics.unittests.domain.model.RepoCommit
import java.time.Instant

class RepoCommitMapper {

    fun mapDomainToStorage(commit: RepoCommit): RepoCommitEntity {
        return RepoCommitEntity(
            id = commit.sha,
            committer = commit.committer,
            timestamp = commit.timestamp,
            message = commit.message,
            repoId = commit.repoId
        )
    }

    fun mapStorageToDomain(commit: RepoCommitEntity): RepoCommit {
        return RepoCommit(
            sha = commit.id,
            repoId = commit.repoId,
            committer = commit.committer,
            timestamp = commit.timestamp,
            message = commit.message
        )
    }

    fun mapApiToDomain(response: RepoCommitsResponse, repoId: Long): RepoCommit {
        return RepoCommit(
            sha = response.sha.orEmpty(),
            committer = response.committer?.login.orEmpty(),
            timestamp = Instant.parse(response.commit?.committer?.date).toEpochMilli().orZero(),
            message = response.commit?.message.orEmpty(),
            repoId = repoId
        )
    }
}