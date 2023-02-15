package com.dmitriisemeniuc.unittests.data.source.mapper

import com.dmitriisemeniuc.unittests.common.extensions.orZero
import com.dmitriisemeniuc.unittests.data.api.model.response.CommitsResponse
import com.dmitriisemeniuc.unittests.data.db.entities.CommitEntity
import com.dmitriisemeniuc.unittests.domain.model.Commit
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