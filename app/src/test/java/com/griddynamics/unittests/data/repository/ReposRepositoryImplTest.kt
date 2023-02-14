package com.griddynamics.unittests.data.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.griddynamics.unittests.common.net.ApiResponse
import com.griddynamics.unittests.common.net.NetworkFailure
import com.griddynamics.unittests.common.net.NotFoundException
import com.griddynamics.unittests.common.net.Result
import com.griddynamics.unittests.data.api.GitHubApi
import com.griddynamics.unittests.data.api.model.response.RepoResponse
import com.griddynamics.unittests.data.db.dao.ReposDao
import com.griddynamics.unittests.data.db.entities.RepoEntity
import com.griddynamics.unittests.data.source.local.ReposLocalDataSource
import com.griddynamics.unittests.data.source.local.ReposLocalDataSourceImpl
import com.griddynamics.unittests.data.source.mapper.RepoMapper
import com.griddynamics.unittests.data.source.remote.ReposRemoteDataSource
import com.griddynamics.unittests.data.source.remote.ReposRemoteDataSourceImpl
import com.griddynamics.unittests.domain.model.Repo
import com.griddynamics.unittests.helper.captureValues
import com.griddynamics.unittests.presentation.extensions.isNetworkAvailable
import com.griddynamics.unittests.presentation.util.CacheTimeLimiter
import com.griddynamics.unittests.util.TestUtil
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class ReposRepositoryImplTest {

    companion object {
        const val USER = "test_user"
    }

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testScope = TestScope()
    private val testDispatcher = UnconfinedTestDispatcher(testScope.testScheduler)

    private var mockContext = mockk<Context>(relaxed = true)
    private var mockReposDao = mockk<ReposDao>()
    private lateinit var reposLocalDataSource: ReposLocalDataSource
    private var mockGitHubApi = mockk<GitHubApi>()
    private lateinit var reposRemoteDataSource: ReposRemoteDataSource
    private var reposMapper = RepoMapper()
    private var mockCacheTimeLimiter = mockk<CacheTimeLimiter<String>>()
    private lateinit var mockedDomainModelsData: List<Repo>
    private lateinit var mockedApiResponseData: List<RepoResponse>
    private lateinit var mockedDbModelsData: List<RepoEntity>

    // Class under test
    private lateinit var repository: ReposRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        reposRemoteDataSource = ReposRemoteDataSourceImpl(mockGitHubApi)
        reposLocalDataSource = ReposLocalDataSourceImpl(mockReposDao)
        repository = ReposRepositoryImpl(
            context = mockContext,
            reposRemoteDataSource = reposRemoteDataSource,
            reposLocalDataSource = reposLocalDataSource,
            reposMapper = reposMapper,
            cacheTimeLimiter = mockCacheTimeLimiter,
            dispatcher = testDispatcher
        )
        coEvery { mockReposDao.insertAll(any()) } just Runs
        mockedDomainModelsData = TestUtil.generateRepoDomainModels(USER, count = 2)
        mockedApiResponseData = TestUtil.generateReposApiResponse(USER, 2)
        mockedDbModelsData = TestUtil.generateReposDbModels(USER, count = 2)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDataFromNetwork Success Test`() = testScope.runTest {
        // given
        every { mockContext.isNetworkAvailable() } returns true
        coEvery { mockGitHubApi.getReposByUser(USER) } returns mockedApiResponseData
        val expected = mockedDomainModelsData
        // when
        val actual = repository.loadDataFromNetwork(USER)
        // then
        assert(actual is ApiResponse.Success)
        assertThat(expected, `is`((actual as ApiResponse.Success).data))
    }

    @Test
    fun `loadDataFromNetwork Returns NetworkFailure Error Test`() = testScope.runTest {
        // given
        every { mockContext.isNetworkAvailable() } returns false
        // when
        val actual = repository.loadDataFromNetwork(USER)
        // then
        assert(actual is ApiResponse.Error)
        assert((actual as ApiResponse.Error).reason is NetworkFailure)
    }

    @Test
    fun `loadDataFromNetwork Returns Server Error Test`() = testScope.runTest {
        // given
        every { mockContext.isNetworkAvailable() } returns true
        coEvery { mockGitHubApi.getReposByUser(USER) } throws IOException("Server Error")
        // when
        val actual = repository.loadDataFromNetwork(USER)
        // then
        assert(actual is ApiResponse.Error)
    }

    @Test
    fun `loadDataFromNetwork Returns NotFoundException Test`() = testScope.runTest {
        // given
        every { mockContext.isNetworkAvailable() } returns true
        coEvery { mockGitHubApi.getReposByUser(USER) } returns emptyList()
        // when
        val actual = repository.loadDataFromNetwork(USER)
        // then
        assert(actual is ApiResponse.Error)
        assert((actual as ApiResponse.Error).reason is NotFoundException)
    }

    @Test
    fun `loadDataFromDb Success Test`() = testScope.runTest {
        // given
        every { mockContext.isNetworkAvailable() } returns false
        coEvery { mockReposDao.findByUser(USER) } returns mockedDbModelsData
        val expected = mockedDomainModelsData
        // when
        val actual = repository.loadDataFromDb(USER)
        // then
        assertThat(expected, `is`(actual))
    }

    @Test
    fun `saveData Test`() = testScope.runTest {
        // given
        val dataToSave = mockedDomainModelsData
        // when
        repository.saveData(dataToSave)
        // then
        coVerify(exactly = 1) { reposLocalDataSource.saveRepos(any()) }
        coVerify(exactly = 1) { mockReposDao.insertAll(any()) }
    }

    @Test
    fun `getReposByUser LoadFromDb Success Test`() = testScope.runTest {
        // given
        every { mockContext.isNetworkAvailable() } returns true
        coEvery { mockReposDao.findByUser(USER) } returns mockedDbModelsData
        every { mockCacheTimeLimiter.shouldFetch(key = any()) } returns false
        // when
        repository.getReposByUser(USER).captureValues {
            // then
            val expectedData = mockedDomainModelsData
            assertThat(values.size, `is`(1))
            assertThat(Result.Success(expectedData), `is`(values.single()))
        }
    }

    @Test
    fun `getReposByUser LoadFromNetwork Success Test`() = testScope.runTest {
        // given
        every { mockContext.isNetworkAvailable() } returns true
        coEvery { mockReposDao.findByUser(USER) } returns emptyList()
        every { mockCacheTimeLimiter.shouldFetch(key = any()) } returns true
        coEvery { mockReposDao.findByUser(USER) } returns mockedDbModelsData
        coEvery { mockGitHubApi.getReposByUser(USER) } returns mockedApiResponseData
        val expectedData = mockedDomainModelsData
        // when
        repository.getReposByUser(USER).captureValues {
            // then
            assertThat(values.size, `is`(2))
            assertThat(Result.Loading(null), `is`(values.first()))
            assertThat(Result.Success(expectedData), `is`(values.last()))
        }
    }

    @Test
    fun `getReposByUser LoadFromNetwork Server Error Test`() = testScope.runTest {
        // given
        val expectedError = IOException("Server error")
        every { mockContext.isNetworkAvailable() } returns true
        coEvery { mockReposDao.findByUser(user = USER) } returns emptyList()
        every { mockCacheTimeLimiter.shouldFetch(key = any()) } returns true
        every { mockCacheTimeLimiter.reset(key = any()) } just Runs
        coEvery { mockGitHubApi.getReposByUser(user = USER) } throws expectedError
        // when
        repository.getReposByUser(user = USER).captureValues {
            // then
            assertThat(values.size, `is`(2))
            assertThat(Result.Loading(null), `is`(values.first()))
            assertThat(Result.Error(expectedError), `is`(values.last()))
        }
    }

    @Test
    fun `getReposByUser LoadFromNetwork NetworkFailure Error Test`() = testScope.runTest {
        // given
        every { mockContext.isNetworkAvailable() } returns false
        coEvery { mockReposDao.findByUser(USER) } returns emptyList()
        every { mockCacheTimeLimiter.shouldFetch(key = any()) } returns true
        every { mockCacheTimeLimiter.reset(key = any()) } just Runs
        // when
        repository.getReposByUser(USER).captureValues {
            // then
            assertThat(values.size, `is`(2))
            assert(values.first() is Result.Loading)
            assert(values.last() is Result.Error)
            assert((values.last() as Result.Error).error is NetworkFailure)
        }
    }
}