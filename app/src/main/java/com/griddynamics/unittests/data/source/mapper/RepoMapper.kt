package com.griddynamics.unittests.data.source.mapper

import com.griddynamics.unittests.common.extensions.orZero
import com.griddynamics.unittests.data.api.model.response.RepoResponse
import com.griddynamics.unittests.data.db.entities.RepoEntity
import com.griddynamics.unittests.domain.model.Repo

class RepoMapper {

    fun mapDomainToStorage(repo: Repo): RepoEntity {
        return RepoEntity(
            id = repo.id.orZero(),
            name = repo.name,
            description = repo.description,
            user = repo.user
        )
    }

    fun mapStorageToDomain(repo: RepoEntity): Repo {
        return Repo(
            id = repo.id,
            name = repo.name,
            description = repo.description,
            user = repo.user
        )
    }

    fun mapApiToDomain(repo: RepoResponse): Repo {
        return Repo(
            id = repo.id.orZero(),
            name = repo.name.orEmpty(),
            description = repo.description,
            user = repo.owner?.login.orEmpty()
        )
    }
}