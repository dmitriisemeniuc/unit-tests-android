package com.griddynamics.unittests.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.griddynamics.unittests.data.db.AppDatabase
import com.griddynamics.unittests.data.db.entities.RepoEntity
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
class ReposLocalDataSourceImplTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: ReposLocalDataSource
    private lateinit var database: AppDatabase

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource =
            ReposLocalDataSourceImpl(
                database.getReposDao()
            )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveRepoAndRetrieveRepo() = runBlocking {
        // given - A new repo saved in the database.
        val repo =
            RepoEntity(RepoEntity.ID, RepoEntity.NAME, RepoEntity.DESCRIPTION, RepoEntity.USER)
        localDataSource.saveRepo(repo)

        // when  - Repo retrieved by ID.
        val loaded = localDataSource.getRepoById(repo.id)

        // then - The loaded data contains the expected values.
        assertThat(loaded as RepoEntity, CoreMatchers.notNullValue())
        assertThat(loaded.id, `is`(repo.id))
        assertThat(loaded.name, `is`(repo.name))
        assertThat(loaded.description, `is`(repo.description))
        assertThat(loaded.user, `is`(repo.user))
    }

    @Test
    fun saveAndUpdateAndRetrieveRepo() = runBlocking {
        // given - A new repo saved in the database.
        val repo =
            RepoEntity(RepoEntity.ID, RepoEntity.NAME, RepoEntity.DESCRIPTION, RepoEntity.USER)
        localDataSource.saveRepo(repo)

        // Update the Repo
        repo.apply {
            name = "updated name"
            description = "updated description"
            user = "updated user"
        }
        localDataSource.updateRepo(repo)
        // when - Get the repo by id from the database.
        val loaded = localDataSource.getRepoById(repo.id)

        // then - The loaded data contains the expected values.
        assertThat(loaded as RepoEntity, CoreMatchers.notNullValue())
        assertThat(loaded.id, `is`(repo.id))
        assertThat(loaded.name, `is`(repo.name))
        assertThat(loaded.description, `is`(repo.description))
        assertThat(loaded.user, `is`(repo.user))
    }
}