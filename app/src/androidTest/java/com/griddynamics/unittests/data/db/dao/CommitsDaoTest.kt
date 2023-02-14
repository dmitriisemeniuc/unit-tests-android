package com.griddynamics.unittests.data.db.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.griddynamics.unittests.data.db.AppDatabase
import com.griddynamics.unittests.data.db.entities.CommitEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CommitsDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertCommitAndGetById() = runBlocking {
        // given - Insert a commit.
        val commit = CommitEntity(
            CommitEntity.ID,
            CommitEntity.REPO_ID,
            CommitEntity.COMMITTER,
            CommitEntity.TIMESTAMP,
            CommitEntity.MESSAGE
        )
        database.getCommitsDao().insert(commit)

        // when - Get the commit by id from the database.
        val loaded = database.getCommitsDao().findById(commit.id)

        // then - The loaded data contains the expected values.
        assertThat(loaded as CommitEntity, CoreMatchers.notNullValue())
        assertThat(loaded.id, `is`(commit.id))
        assertThat(loaded.message, `is`(commit.message))
        assertThat(loaded.committer, `is`(commit.committer))
        assertThat(loaded.timestamp, `is`(commit.timestamp))
        assertThat(loaded.repoId, `is`(commit.repoId))
    }

    @Test
    fun updateCommitAndGetById() = runBlocking {
        // given - Insert a commit.
        val commit = CommitEntity(
            CommitEntity.ID,
            CommitEntity.REPO_ID,
            CommitEntity.COMMITTER,
            CommitEntity.TIMESTAMP,
            CommitEntity.MESSAGE
        )
        database.getCommitsDao().insert(commit)
        // Update the Commit
        commit.apply {
            message = "updated message"
            repoId = 999L
            committer = "updated committer"
            timestamp = 1L
        }
        database.getCommitsDao().update(commit)
        // when - Get the commit by id from the database.
        val loaded = database.getCommitsDao().findById(commit.id)

        // then - The loaded data contains the expected values.
        assertThat(loaded as CommitEntity, CoreMatchers.notNullValue())
        assertThat(loaded.id, `is`(commit.id))
        assertThat(loaded.message, `is`(commit.message))
        assertThat(loaded.timestamp, `is`(commit.timestamp))
        assertThat(loaded.committer, `is`(commit.committer))
        assertThat(loaded.repoId, `is`(commit.repoId))
    }
}