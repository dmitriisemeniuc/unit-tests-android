package com.dmitriisemeniuc.unittests.data.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dmitriisemeniuc.unittests.common.net.ApiResponse
import com.dmitriisemeniuc.unittests.common.net.NetworkFailure
import com.dmitriisemeniuc.unittests.common.net.NotFoundException
import com.dmitriisemeniuc.unittests.common.net.Result
import com.dmitriisemeniuc.unittests.data.api.GitHubApi
import com.dmitriisemeniuc.unittests.data.api.model.request.CommitsParams
import com.dmitriisemeniuc.unittests.data.api.model.response.CommitsResponse
import com.dmitriisemeniuc.unittests.data.db.dao.CommitsDao
import com.dmitriisemeniuc.unittests.data.db.entities.CommitEntity
import com.dmitriisemeniuc.unittests.data.source.local.CommitsLocalDataSource
import com.dmitriisemeniuc.unittests.data.source.local.CommitsLocalDataSourceImpl
import com.dmitriisemeniuc.unittests.data.source.mapper.CommitMapper
import com.dmitriisemeniuc.unittests.data.source.remote.CommitsRemoteDataSource
import com.dmitriisemeniuc.unittests.data.source.remote.CommitsRemoteDataSourceImpl
import com.dmitriisemeniuc.unittests.domain.model.Commit
import com.dmitriisemeniuc.unittests.domain.model.Repo
import com.dmitriisemeniuc.unittests.helper.captureValues
import com.dmitriisemeniuc.unittests.presentation.extensions.isNetworkAvailable
import com.dmitriisemeniuc.unittests.presentation.util.CacheTimeLimiter
import com.dmitriisemeniuc.unittests.util.TestUtil
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.hamcrest.core.Is.`is`
import org.hamcrest.junit.MatcherAssert.assertThat
import org.junit.*
import java.io.IOException

@ExperimentalCoroutinesApi
class CommitsRepositoryImplTest {

    companion object {
        const val USER = "test_user"
        const val REPO_ID = 100L
        const val REPO = "test_repo"
    }

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testScope = TestScope()
    private val testDispatcher = UnconfinedTestDispatcher(testScope.testScheduler)

    private var mockContext = mockk<Context>(relaxed = true)
    private var mockCommitsDao = mockk<CommitsDao>()
    private lateinit var commitsLocalDataSource: CommitsLocalDataSource
    private var mockGitHubApi = mockk<GitHubApi>()
    private lateinit var commitsRemoteDataSource: CommitsRemoteDataSource
    private var commitMapper = CommitMapper()
    private var mockCacheTimeLimiter = mockk<CacheTimeLimiter<Long>>()
    private lateinit var mockedDomainModelsData: List<Commit>
    private lateinit var mockedApiResponseData: List<CommitsResponse>
    private lateinit var mockedDbModelsData: List<CommitEntity>
    private lateinit var params: CommitsParams

    // Class under test
    private lateinit var repository: CommitsRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        params = CommitsParams(user = USER, repo = REPO, repoId = REPO_ID)
        commitsRemoteDataSource = CommitsRemoteDataSourceImpl(mockGitHubApi)
        commitsLocalDataSource = CommitsLocalDataSourceImpl(mockCommitsDao)
        repository = CommitsRepositoryImpl(
            context = mockContext,
            commitsRemoteDataSource = commitsRemoteDataSource,
            commitsLocalDataSource = commitsLocalDataSource,
            commitMapper = commitMapper,
            cacheTimeLimiter = mockCacheTimeLimiter,
            dispatcher = testDispatcher
        )
        coEvery { mockCommitsDao.insertAll(any()) } just Runs
        mockedDomainModelsData = TestUtil.generateCommitDomainModels(
            committer = params.user,
            repoId = params.repoId, count = 2
        )
        mockedApiResponseData = TestUtil.generateCommitsApiResponse(params.user, count = 2)
        mockedDbModelsData = TestUtil.generateCommitDbModels(params.user, params.repoId, count = 2)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDataFromNetwork Success Test`() = testScope.runTest {
        // given
        every { mockContext.isNetworkAvailable() } returns true
        coEvery {
            mockGitHubApi.getCommitsByUserAndRepo(
                params.user, params.repo
            )
        } returns mockedApiResponseData
        val expected = mockedDomainModelsData
        // when
        val actual = repository.loadDataFromNetwork(params)
        // then
        assert(actual is ApiResponse.Success)
        assertThat(expected, `is`((actual as ApiResponse.Success).data))
    }

    @Test
    fun `loadDataFromNetwork Returns NetworkFailure Error Test`() = testScope.runTest {
        // given
        every { mockContext.isNetworkAvailable() } returns false
        // when
        val actual = repository.loadDataFromNetwork(params)
        // then
        assert(actual is ApiResponse.Error)
        assert((actual as ApiResponse.Error).reason is NetworkFailure)
    }

    @Test
    fun `loadDataFromNetwork Returns Server Error Test`() = testScope.runTest {
        // given
        every { mockContext.isNetworkAvailable() } returns true
        coEvery {
            mockGitHubApi.getCommitsByUserAndRepo(
                params.user,
                params.repo
            )
        } throws IOException("Server Error")
        // when
        val actual = repository.loadDataFromNetwork(params)
        // then
        assert(actual is ApiResponse.Error)
    }

    @Test
    fun `loadDataFromNetwork Returns NotFoundException Test`() = testScope.runTest {
        // given
        every { mockContext.isNetworkAvailable() } returns true
        coEvery {
            mockGitHubApi.getCommitsByUserAndRepo(
                params.user,
                params.repo
            )
        } returns emptyList()
        // when
        val actual = repository.loadDataFromNetwork(params)
        // then
        assert(actual is ApiResponse.Error)
        assert((actual as ApiResponse.Error).reason is NotFoundException)
    }

    @Test
    fun `saveData Test`() = testScope.runTest {
        // given
        val dataToSave = mockedDomainModelsData
        // when
        repository.saveData(dataToSave)
        // then
        coVerify(exactly = 1) { commitsLocalDataSource.saveCommits(any()) }
        coVerify(exactly = 1) { mockCommitsDao.insertAll(any()) }
    }

    @Test
    fun `getCommitsByOwnerAndRepo LoadFromDb Success Test`() = testScope.runTest {
        // given
        every { mockContext.isNetworkAvailable() } returns true
        coEvery { mockCommitsDao.findByRepoId(params.repoId) } returns mockedDbModelsData
        every { mockCacheTimeLimiter.shouldFetch(key = any()) } returns false
        // when
        repository.getCommitsByOwnerAndRepo(params).captureValues {
            // then
            val expectedData = mockedDomainModelsData
            assertThat(values.size, `is`(1))
            assertThat(Result.Success(expectedData), `is`(values.single()))
        }
    }

    @Test
    fun `getCommitsByOwnerAndRepo LoadFromNetwork Success Test`() = testScope.runTest {
        // given
        every { mockContext.isNetworkAvailable() } returns true
        every { mockCacheTimeLimiter.shouldFetch(key = any()) } returns true
        coEvery { mockCommitsDao.findByRepoId(params.repoId) } returns mockedDbModelsData
        coEvery {
            mockGitHubApi.getCommitsByUserAndRepo(
                params.user,
                params.repo
            )
        } returns mockedApiResponseData
        val expectedData = mockedDomainModelsData
        // when
        repository.getCommitsByOwnerAndRepo(params).captureValues {
            // then
            assertThat(values.size, `is`(2))
            assertThat(Result.Loading(null), `is`(values.first()))
            assertThat(Result.Success(expectedData), `is`(values.last()))
        }
    }

    @Test
    fun `getCommitsByOwnerAndRepo LoadFromNetwork Server Error Test`() = testScope.runTest {
        // given
        val expectedError = IOException("Server error")
        every { mockContext.isNetworkAvailable() } returns true
        coEvery { mockCommitsDao.findByRepoId(params.repoId) } returns emptyList()
        every { mockCacheTimeLimiter.shouldFetch(key = any()) } returns true
        every { mockCacheTimeLimiter.reset(key = any()) } just Runs
        coEvery {
            mockGitHubApi.getCommitsByUserAndRepo(
                params.user,
                params.repo
            )
        } throws expectedError
        // when
        repository.getCommitsByOwnerAndRepo(params).captureValues {
            // then
            assertThat(values.size, `is`(2))
            assertThat(Result.Loading(null), `is`(values.first()))
            assertThat(Result.Error<List<Repo>>(expectedError), `is`(values.last()))
        }
    }

    @Test
    fun `getCommitsByOwnerAndRepo LoadFromNetwork NetworkFailure Error Test`() = testScope.runTest {
        // given
        every { mockContext.isNetworkAvailable() } returns false
        coEvery { mockCommitsDao.findByRepoId(params.repoId) } returns emptyList()
        every { mockCacheTimeLimiter.shouldFetch(key = any()) } returns true
        every { mockCacheTimeLimiter.reset(key = any()) } just Runs
        // when
        repository.getCommitsByOwnerAndRepo(params).captureValues {
            // then
            assertThat(values.size, `is`(2))
            assert(values.first() is Result.Loading)
            assert(values.last() is Result.Error)
            assert((values.last() as Result.Error).error is NetworkFailure)
        }
    }
}