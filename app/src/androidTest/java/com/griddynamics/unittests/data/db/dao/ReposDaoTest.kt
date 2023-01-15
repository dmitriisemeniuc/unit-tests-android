package com.griddynamics.unittests.data.db.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.griddynamics.unittests.data.db.AppDatabase
import com.griddynamics.unittests.data.db.entities.ReposEntity
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
        // GIVEN - Insert a repo.
        val repo = ReposEntity(ID, NAME, DESCRIPTION, USER)
        database.getReposDao().insert(repo)

        // WHEN - Get the repo by id from the database.
        val loaded = database.getReposDao().findById(repo.id)

        // THEN - The loaded data contains the expected values.
        MatcherAssert.assertThat(loaded as ReposEntity, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.id, `is`(repo.id))
        MatcherAssert.assertThat(loaded.name, `is`(repo.name))
        MatcherAssert.assertThat(loaded.description, `is`(repo.description))
        MatcherAssert.assertThat(loaded.user, `is`(repo.user))
    }

    @Test
    fun updateRepoAndGetById() = runBlocking {
        // GIVEN - Insert a repo.
        val repo = ReposEntity(ID, NAME, DESCRIPTION, USER)
        database.getReposDao().insert(repo)
        // Update the Repo
        repo.apply {
            name = "updated name"
            description = "updated description"
            user = "updated user"
        }
        database.getReposDao().update(repo)
        // WHEN - Get the repo by id from the database.
        val loaded = database.getReposDao().findById(repo.id)

        // THEN - The loaded data contains the expected values.
        MatcherAssert.assertThat(loaded as ReposEntity, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.id, `is`(repo.id))
        MatcherAssert.assertThat(loaded.name, `is`(repo.name))
        MatcherAssert.assertThat(loaded.description, `is`(repo.description))
        MatcherAssert.assertThat(loaded.user, `is`(repo.user))
    }

    companion object {
        private const val ID = 1L
        private const val NAME = "name"
        private const val DESCRIPTION = "description"
        private const val USER = "test_user"
    }
}