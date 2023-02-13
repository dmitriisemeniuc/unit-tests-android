package com.griddynamics.unittests.presentation.search.adapter

import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.griddynamics.unittests.common.extensions.isTrue
import com.griddynamics.unittests.databinding.LayoutItemRepoBinding
import com.griddynamics.unittests.domain.model.Repo

class RepoViewHolder(val binding: LayoutItemRepoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(repo: Repo) = itemView.run {
        binding.tvRepoName.text = repo.name
        if (repo.description.isNullOrEmpty().isTrue()) {
            binding.tvRepoDescription.isGone = true
        } else {
            binding.tvRepoDescription.apply {
                isVisible = true
                text = repo.description
            }
        }
    }
}