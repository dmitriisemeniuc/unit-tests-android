package com.griddynamics.unittests.presentation.commits.viewmodel

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.griddynamics.unittests.common.net.NetworkFailure
import com.griddynamics.unittests.common.net.Result
import com.griddynamics.unittests.data.api.GitHubApi
import com.griddynamics.unittests.data.api.model.request.CommitsParams
import com.griddynamics.unittests.data.api.model.response.CommitsResponse
import com.griddynamics.unittests.data.db.dao.CommitsDao
import com.griddynamics.unittests.data.db.entities.CommitEntity
import com.griddynamics.unittests.data.repository.CommitsRepositoryImpl
import com.griddynamics.unittests.data.source.local.CommitsLocalDataSource
import com.griddynamics.unittests.data.source.local.CommitsLocalDataSourceImpl
import com.griddynamics.unittests.data.source.mapper.CommitMapper
import com.griddynamics.unittests.data.source.remote.CommitsRemoteDataSource
import com.griddynamics.unittests.data.source.remote.CommitsRemoteDataSourceImpl
import com.griddynamics.unittests.domain.model.Commit
import com.griddynamics.unittests.domain.usecase.GetCommitsUseCase
import com.griddynamics.unittests.helper.captureValues
import com.griddynamics.unittests.helper.getOrAwaitValue
import com.griddynamics.unittests.presentation.extensions.isNetworkAvailable
import com.griddynamics.unittests.presentation.util.CacheTimeLimiter
import com.griddynamics.unittests.util.TestUtil
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class CommitsViewModelTest {

    companion object {
        const val USER = "test_user"
        const val REPO_ID = 100L
        const val REPO = "test_repo"
    }

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testScope = TestScope()
    private val testDispatcher = UnconfinedTestDispatcher(testScope.testScheduler)

    private val mockedContext = mockk<Context>(relaxed = true)
    private val mockedApplication = mockk<Application>(relaxed = true)
    private var mockedApi = mockk<GitHubApi>()
    private var mockedCommitsDao = mockk<CommitsDao>()
    private var commitMapper = CommitMapper()
    private var commitsRepository = mockk<CommitsRepositoryImpl>()
    private var cacheTimeLimiter = mockk<CacheTimeLimiter<Long>>()

    private var savedStateHandle = SavedStateHandle()
    private lateinit var getCommitsUseCase: GetCommitsUseCase
    private lateinit var commitsRemoteDataSource: CommitsRemoteDataSource
    private lateinit var reposLocalDataSource: CommitsLocalDataSource
    private lateinit var mockedApiResponse: List<CommitsResponse>
    private lateinit var mockedDbResponse: List<CommitEntity>
    private lateinit var mockedCommits: List<Commit>
    private lateinit var params: CommitsParams

    // Class under test
    private lateinit var viewModel: CommitsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        params = CommitsParams(user = USER, repo = REPO, repoId = REPO_ID)
        commitsRemoteDataSource = CommitsRemoteDataSourceImpl(mockedApi)
        reposLocalDataSource = CommitsLocalDataSourceImpl(mockedCommitsDao)
        commitsRepository = CommitsRepositoryImpl(
            context = mockedContext,
            commitsRemoteDataSource = commitsRemoteDataSource,
            commitsLocalDataSource = reposLocalDataSource,
            commitMapper = commitMapper,
            dispatcher = testDispatcher,
            cacheTimeLimiter = cacheTimeLimiter
        )
        getCommitsUseCase = GetCommitsUseCase(commitsRepository)
        viewModel = CommitsViewModel(
            application = mockedApplication,
            getCommitsUseCase = getCommitsUseCase,
            savedStateHandle = savedStateHandle
        )
        mockedApiResponse = TestUtil.generateCommitsApiResponse(params.user, count = 2)
        mockedDbResponse =
            TestUtil.generateCommitDbModels(user = params.user, repoId = params.repoId, count = 2)
        mockedCommits = TestUtil.generateCommitDomainModels(
            committer = params.user,
            repoId = params.repoId,
            count = 2
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `findCommitsByOwnerAndRepo From Network Loading Is Shown Before Content Test`() =
        testScope.runTest {
            // given
            coEvery { mockedCommitsDao.findByRepoId(repoId = any()) } returns emptyList()
            // when
            viewModel.findCommitsByOwnerAndRepo(params.user, params.repo, params.repoId)
            // then
            val actual = viewModel.commits.getOrAwaitValue()
            assert(actual is Result.Loading)
        }

    @Test
    fun `findCommitsByOwnerAndRepo From Database Success Test`() = testScope.runTest {
        // given
        coEvery { mockedCommitsDao.findByRepoId(repoId = any()) } returns mockedDbResponse
        coEvery { cacheTimeLimiter.shouldFetch(key = any()) } returns false
        // when
        viewModel.findCommitsByOwnerAndRepo(
            owner = params.user,
            repo = params.repo,
            repoId = params.repoId
        )
        // then
        val expected = mockedCommits
        viewModel.commits.captureValues {
            Assert.assertEquals(values.size, 1)
            assert(values.single() is Result.Success)
            Assert.assertEquals(expected, (values.single() as Result.Success<List<Commit>>).data)
        }
    }

    @Test
    fun `findCommitsByOwnerAndRepo From Network NetworkFailure Error Test`() = testScope.runTest {
        // given
        coEvery { mockedCommitsDao.findByRepoId(repoId = any()) } returns emptyList()
        coEvery { cacheTimeLimiter.shouldFetch(key = any()) } returns true
        coEvery { cacheTimeLimiter.reset(key = any()) } just Runs
        every { mockedContext.isNetworkAvailable() } returns false
        // when
        viewModel.findCommitsByOwnerAndRepo(
            owner = params.user,
            repo = params.repo,
            repoId = params.repoId
        )
        // then
        viewModel.commits.captureValues {
            Assert.assertEquals(values.size, 2)
            assert(values.first() is Result.Loading)
            assert(values.last() is Result.Error)
            assert((values.last() as Result.Error).error is NetworkFailure)
        }
    }

    @Test
    fun `findCommitsByOwnerAndRepo From Network Success Test`() = testScope.runTest {
        // given
        coEvery {
            mockedApi.getCommitsByUserAndRepo(
                user = any(),
                repo = any()
            )
        } returns mockedApiResponse
        coEvery { mockedCommitsDao.findByRepoId(repoId = any()) } returns mockedDbResponse
        coEvery { mockedCommitsDao.insertAll(commits = any()) } just Runs
        coEvery { cacheTimeLimiter.shouldFetch(key = any()) } returns true
        coEvery { cacheTimeLimiter.reset(key = any()) } just Runs
        every { mockedContext.isNetworkAvailable() } returns true
        // when
        viewModel.findCommitsByOwnerAndRepo(
            owner = params.user,
            repo = params.repo,
            repoId = params.repoId
        )
        // then
        val expected = mockedCommits
        viewModel.commits.captureValues {
            Assert.assertEquals(values.size, 2)
            assert(values.first() is Result.Loading)
            assert(values.last() is Result.Success)
            Assert.assertEquals(expected, (values.last() as Result.Success<List<Commit>>).data)
        }
    }
}