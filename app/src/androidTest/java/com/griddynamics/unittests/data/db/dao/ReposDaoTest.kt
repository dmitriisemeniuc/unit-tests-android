package com.griddynamics.unittests.data.db.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.griddynamics.unittests.data.db.AppDatabase
import com.griddynamics.unittests.data.db.entities.RepoEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ReposDaoTest {

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
    fun insertRepoAndGetById() = runBlocking {
        // given - Insert a repo.
        val repo =
            RepoEntity(RepoEntity.ID, RepoEntity.NAME, RepoEntity.DESCRIPTION, RepoEntity.USER)
        database.getReposDao().insert(repo)

        // when - Get the repo by id from the database.
        val loaded = database.getReposDao().findById(repo.id)

        // then - The loaded data contains the expected values.
        MatcherAssert.assertThat(loaded as RepoEntity, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.id, `is`(repo.id))
        MatcherAssert.assertThat(loaded.name, `is`(repo.name))
        MatcherAssert.assertThat(loaded.description, `is`(repo.description))
        MatcherAssert.assertThat(loaded.user, `is`(repo.user))
    }

    @Test
    fun updateRepoAndGetById() = runBlocking {
        // given - Insert a repo.
        val repo =
            RepoEntity(RepoEntity.ID, RepoEntity.NAME, RepoEntity.DESCRIPTION, RepoEntity.USER)
        database.getReposDao().insert(repo)
        // Update the Repo
        repo.apply {
            name = "updated name"
            description = "updated description"
            user = "updated user"
        }
        database.getReposDao().update(repo)
        // when - Get the repo by id from the database.
        val loaded = database.getReposDao().findById(repo.id)

        // then - The loaded data contains the expected values.
        MatcherAssert.assertThat(loaded as RepoEntity, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.id, `is`(repo.id))
        MatcherAssert.assertThat(loaded.name, `is`(repo.name))
        MatcherAssert.assertThat(loaded.description, `is`(repo.description))
        MatcherAssert.assertThat(loaded.user, `is`(repo.user))
    }
}