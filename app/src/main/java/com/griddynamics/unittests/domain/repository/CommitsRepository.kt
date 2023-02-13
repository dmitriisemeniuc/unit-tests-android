package com.griddynamics.unittests.domain.repository

import androidx.lifecycle.LiveData
import com.griddynamics.unittests.common.net.Result
import com.griddynamics.unittests.data.api.model.request.CommitsParams
import com.griddynamics.unittests.domain.model.Commit

interface CommitsRepository {

    fun getCommitsByOwnerAndRepo(params: CommitsParams): LiveData<Result<List<Commit>>>
}