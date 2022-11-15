package com.griddynamics.unittests.data.source.mapper

import com.griddynamics.unittests.common.extensions.orZero
import com.griddynamics.unittests.data.api.model.RepoResponse
import com.griddynamics.unittests.data.db.entities.ReposEntity
import com.griddynamics.unittests.domain.model.Repo

class ReposMapper {

    fun mapDomainToStorage(repo: Repo): ReposEntity {
        return ReposEntity(
            id = repo.id.orZero(),
            name = repo.name,
            description = repo.description,
            user = repo.user
        )
    }

    fun mapStorageToDomain(repo: ReposEntity): Repo {
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