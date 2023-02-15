package com.dmitriisemeniuc.unittests.data.source.mapper

import com.dmitriisemeniuc.unittests.common.extensions.orZero
import com.dmitriisemeniuc.unittests.data.api.model.response.RepoResponse
import com.dmitriisemeniuc.unittests.data.db.entities.RepoEntity
import com.dmitriisemeniuc.unittests.domain.model.Repo

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