package com.dmitriisemeniuc.unittests.domain.repository

import androidx.lifecycle.LiveData
import com.dmitriisemeniuc.unittests.common.net.Result
import com.dmitriisemeniuc.unittests.data.api.model.request.CommitsParams
import com.dmitriisemeniuc.unittests.domain.model.Commit

interface CommitsRepository {

    fun getCommitsByOwnerAndRepo(params: CommitsParams): LiveData<Result<List<Commit>>>
}