package com.griddynamics.unittests.presentation.commits.adapter

import androidx.recyclerview.widget.RecyclerView
import com.griddynamics.unittests.common.extensions.format
import com.griddynamics.unittests.databinding.LayoutItemCommitBinding
import com.griddynamics.unittests.domain.model.Commit
import java.util.Date

class RepoCommitViewHolder(private val binding: LayoutItemCommitBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(commit: Commit) = itemView.run {
        with(binding) {
            tvMessage.text = commit.message
            tvSha.text = commit.sha
            tvCommitter.text = commit.committer
            tvDate.text = Date(commit.timestamp).format()
        }
    }
}