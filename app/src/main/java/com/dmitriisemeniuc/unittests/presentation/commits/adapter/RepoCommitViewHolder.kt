package com.dmitriisemeniuc.unittests.presentation.commits.adapter

import androidx.recyclerview.widget.RecyclerView
import com.dmitriisemeniuc.unittests.common.extensions.format
import com.dmitriisemeniuc.unittests.domain.model.Commit
import com.dmitriisemeniuc.unittests.databinding.LayoutItemCommitBinding
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