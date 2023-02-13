package com.griddynamics.unittests.domain.repository

import androidx.lifecycle.LiveData
import com.griddynamics.unittests.common.net.Result
import com.griddynamics.unittests.data.api.model.request.RepoCommitsParams
import com.griddynamics.unittests.domain.model.RepoCommit

interface RepoCommitsRepository {
    fun getCommitsByOwnerAndRepo(params: RepoCommitsParams): LiveData<Result<List<RepoCommit>>>
}