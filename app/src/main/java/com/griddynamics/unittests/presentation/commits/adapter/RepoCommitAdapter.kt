package com.griddynamics.unittests.presentation.commits.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.griddynamics.unittests.databinding.LayoutItemCommitBinding
import com.griddynamics.unittests.domain.model.Commit

class RepoCommitAdapter(
) : ListAdapter<Commit, RepoCommitViewHolder>(REPO_COMMIT_COMPARATOR) {

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

        private val REPO_COMMIT_COMPARATOR = object : DiffUtil.ItemCallback<Commit>() {
            override fun areItemsTheSame(oldItem: Commit, newItem: Commit): Boolean {
                return oldItem.sha == newItem.sha
            }

            override fun areContentsTheSame(oldItem: Commit, newItem: Commit): Boolean {
                return oldItem == newItem
            }
        }
    }
}