package com.griddynamics.unittests.data.source.mapper

import com.griddynamics.unittests.data.api.model.Owner
import com.griddynamics.unittests.data.api.model.response.RepoResponse
import com.griddynamics.unittests.data.db.entities.ReposEntity
import com.griddynamics.unittests.domain.model.Repo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

private const val FAKE_REPO_ID = 123L
private const val FAKE_REPO_NAME = "SpaceX"
private const val FAKE_REPO_USER = "Elon Musk"
private const val FAKE_REPO_DESCRIPTION =
    "SpaceX designs, manufactures and launches advanced rockets and spacecraft."
private const val FAKE_OWNER_ID = 123L
private const val FAKE_OWNER_LOGIN = "Elon Musk"
private const val FAKE_REPO_RESPONSE_ID = 123L
private const val FAKE_REPO_RESPONSE_NAME = "SpaceX"
private const val FAKE_REPO_RESPONSE_DESCRIPTION =
    "SpaceX designs, manufactures and launches advanced rockets and spacecraft."
private const val FAKE_REPO_RESPONSE_FULL_NAME = "Elon Musk"

class ReposMapperTest {

    private lateinit var mapper: ReposMapper

    @Before
    fun setup() {
        mapper = ReposMapper()
    }

    @Test
    fun testMapDomainToStorage() {
        // given
        val repo = createFakeRepo()

        // when
        val entity: ReposEntity = mapper.mapDomainToStorage(repo)

        // then
        assertThat(entity.id).describedAs("id").isEqualTo(repo.id)
        assertThat(entity.user).describedAs("user").isEqualTo(repo.user)
        assertThat(entity.name).describedAs("name").isEqualTo(repo.name)
        assertThat(entity.description).describedAs("description").isEqualTo(repo.description)
    }

    @Test
    fun testMapStorageToDomain() {
        // given
        val entity = createFakeReposEntity()

        // when
        val repo: Repo = mapper.mapStorageToDomain(entity)

        // then
        assertThat(entity.id).describedAs("id").isEqualTo(repo.id)
        assertThat(entity.user).describedAs("user").isEqualTo(repo.user)
        assertThat(entity.name).describedAs("name").isEqualTo(repo.name)
        assertThat(entity.description).describedAs("description").isEqualTo(repo.description)
    }

    @Test
    fun testMapApiToDomain() {
        // given
        val repoResponse = createFakeRepoResponse()

        // when
        val repo: Repo = mapper.mapApiToDomain(repoResponse)

        // then
        assertThat(repoResponse.id).describedAs("id").isEqualTo(repo.id)
        assertThat(repoResponse.owner?.login).describedAs("user").isEqualTo(repo.user)
        assertThat(repoResponse.name).describedAs("name").isEqualTo(repo.name)
        assertThat(repoResponse.description).describedAs("description").isEqualTo(repo.description)
    }

    private fun createFakeRepo(): Repo {
        return Repo(
            id = FAKE_REPO_ID,
            name = FAKE_REPO_NAME,
            description = FAKE_REPO_DESCRIPTION,
            user = FAKE_REPO_USER
        )
    }

    private fun createFakeReposEntity(): ReposEntity {
        return ReposEntity(
            id = FAKE_REPO_ID,
            name = FAKE_REPO_NAME,
            description = FAKE_REPO_DESCRIPTION,
            user = FAKE_REPO_USER
        )
    }

    private fun createFakeRepoResponse(): RepoResponse {
        return RepoResponse(
            id = FAKE_REPO_RESPONSE_ID,
            name = FAKE_REPO_RESPONSE_NAME,
            description = FAKE_REPO_RESPONSE_DESCRIPTION,
            fullName = FAKE_REPO_RESPONSE_FULL_NAME,
            owner = createFakeOwner()
        )
    }


    private fun createFakeOwner(): Owner {
        return Owner(
            login = FAKE_OWNER_LOGIN,
            id = FAKE_OWNER_ID
        )
    }
}