package com.griddynamics.unittests.presentation.commits.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.griddynamics.unittests.databinding.LayoutItemCommitBinding
import com.griddynamics.unittests.domain.model.RepoCommit

class RepoCommitAdapter(
) : ListAdapter<RepoCommit, RepoCommitViewHolder>(REPO_COMMIT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoCommitViewHolder {
        val binding = LayoutItemCommitBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RepoCommitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepoCommitViewHolder, position: Int) {
        val repo = getItem(position)
        holder.bind(repo)
    }

    companion object {

        private val REPO_COMMIT_COMPARATOR = object : DiffUtil.ItemCallback<RepoCommit>() {
            override fun areItemsTheSame(oldItem: RepoCommit, newItem: RepoCommit): Boolean {
                return oldItem.sha == newItem.sha
            }

            override fun areContentsTheSame(oldItem: RepoCommit, newItem: RepoCommit): Boolean {
                return oldItem == newItem
            }
        }
    }
}