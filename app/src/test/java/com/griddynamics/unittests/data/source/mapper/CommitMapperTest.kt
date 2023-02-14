package com.griddynamics.unittests.data.source.mapper

import com.griddynamics.unittests.data.api.model.response.CommitResponse
import com.griddynamics.unittests.data.api.model.response.CommitsResponse
import com.griddynamics.unittests.data.api.model.response.CommitterResponse
import com.griddynamics.unittests.data.db.entities.CommitEntity
import com.griddynamics.unittests.domain.model.Commit
import com.griddynamics.unittests.util.TestUtil.toTimeStamp
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

private const val FAKE_SHA = "12345"
private const val FAKE_TIME_STAMP = 1672531200L
private const val FAKE_COMMIT_MESSAGE =
    "SpaceX designs, manufactures and launches advanced rockets and spacecraft."
private const val FAKE_OWNER_LOGIN = "Elon Musk"
private const val FAKE_REPO_ID = 123L
private const val FAKE_DATE = "2020-01-01T10:00:00Z"

class CommitMapperTest {

    private lateinit var mapper: CommitMapper

    @Before
    fun setup() {
        mapper = CommitMapper()
    }

    @Test
    fun testMapDomainToStorage() {
        // given
        val commit = createFakeCommit()

        // when
        val entity: CommitEntity = mapper.mapDomainToStorage(commit)

        // then
        assertThat(entity.id).isEqualTo(commit.sha)
        assertThat(entity.committer).isEqualTo(commit.committer)
        assertThat(entity.repoId).isEqualTo(commit.repoId)
        assertThat(entity.message).isEqualTo(commit.message)
    }

    @Test
    fun testMapStorageToDomain() {
        // given
        val entity = createFakeCommitEntity()

        // when
        val commit: Commit = mapper.mapStorageToDomain(entity)

        // then
        assertThat(entity.id).isEqualTo(commit.sha)
        assertThat(entity.committer).isEqualTo(commit.committer)
        assertThat(entity.repoId).isEqualTo(commit.repoId)
        assertThat(entity.message).isEqualTo(commit.message)
        assertThat(entity.timestamp).isEqualTo(commit.timestamp)
    }

    @Test
    fun testMapApiToDomain() {
        // given
        val commitsResponse = createFakeCommitsResponse()

        // when
        val commit: Commit = mapper.mapApiToDomain(commitsResponse, FAKE_REPO_ID)

        // then
        assertThat(commitsResponse.sha).isEqualTo(commit.sha)
        assertThat(commitsResponse.commit?.message).isEqualTo(commit.message)
        assertThat(commitsResponse.commit?.committer?.date?.toTimeStamp()).isEqualTo(commit.timestamp)
        assertThat(commitsResponse.committer?.login).isEqualTo(commit.committer)
    }

    private fun createFakeCommit(): Commit {
        return Commit(
            sha = FAKE_SHA,
            committer = FAKE_OWNER_LOGIN,
            timestamp = FAKE_TIME_STAMP,
            message = FAKE_COMMIT_MESSAGE,
            repoId = FAKE_REPO_ID
        )
    }

    private fun createFakeCommitEntity(): CommitEntity {
        return CommitEntity(
            id = FAKE_SHA,
            repoId = FAKE_REPO_ID,
            committer = FAKE_OWNER_LOGIN,
            timestamp = FAKE_TIME_STAMP,
            message = FAKE_COMMIT_MESSAGE
        )
    }

    private fun createFakeCommitsResponse(): CommitsResponse {
        return CommitsResponse(
            sha = FAKE_SHA,
            committer = CommitterResponse(login = FAKE_OWNER_LOGIN),
            commit = CommitResponse(
                message = FAKE_COMMIT_MESSAGE,
                committer = CommitterResponse(date = FAKE_DATE)
            )
        )
    }
}