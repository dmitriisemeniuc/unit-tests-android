package com.griddynamics.unittests.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.griddynamics.unittests.data.db.AppDatabase
import com.griddynamics.unittests.data.db.entities.CommitEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CommitsLocalDataSourceImplTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: CommitsLocalDataSource
    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        localDataSource = CommitsLocalDataSourceImpl(database.getCommitsDao())
    }

    @After
    fun cleanup() {
        database.close()
    }

    @Test
    fun saveCommitAndRetrieveCommit() = runBlocking {
        // given - A new commit saved in the database.
        val commit = CommitEntity(
            CommitEntity.ID,
            CommitEntity.REPO_ID,
            CommitEntity.COMMITTER,
            CommitEntity.TIMESTAMP,
            CommitEntity.MESSAGE
        )
        localDataSource.saveCommit(commit)

        // when - Commit retrieved by ID.
        val loaded = localDataSource.getCommitById(commit.id)

        // then - The loaded data contains the expected values.
        assertThat(loaded as CommitEntity, CoreMatchers.notNullValue())
        assertThat(loaded.id, Is.`is`(commit.id))
        assertThat(loaded.committer, Is.`is`(commit.committer))
        assertThat(loaded.message, Is.`is`(commit.message))
        assertThat(loaded.repoId, Is.`is`(commit.repoId))
        assertThat(loaded.timestamp, Is.`is`(commit.timestamp))
    }

    @Test
    fun saveAndUpdateAndRetrieveCommit() = runBlocking {
        // given - A new commit saved in the database.
        val commit = CommitEntity(
            CommitEntity.ID,
            CommitEntity.REPO_ID,
            CommitEntity.COMMITTER,
            CommitEntity.TIMESTAMP,
            CommitEntity.MESSAGE
        )
        localDataSource.saveCommit(commit)

        // Update the Commit
        commit.apply {
            message = "updated message"
            committer = "updated committer"
            repoId = 999L
        }
        localDataSource.updateCommit(commit)
        // when - Get the commit by id from the database.
        val loaded = localDataSource.getCommitById(commit.id)

        // then - The loaded data contains the expected values.
        assertThat(loaded as CommitEntity, CoreMatchers.notNullValue())
        assertThat(loaded.id, Is.`is`(commit.id))
        assertThat(loaded.message, Is.`is`(commit.message))
        assertThat(loaded.committer, Is.`is`(commit.committer))
        assertThat(loaded.repoId, Is.`is`(commit.repoId))
        assertThat(loaded.timestamp, Is.`is`(commit.timestamp))
    }
}