package com.dmitriisemeniuc.unittests.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.dmitriisemeniuc.unittests.domain.model.Repo
import com.dmitriisemeniuc.unittests.databinding.LayoutItemRepoBinding

class ReposAdapter(
    private val callback: ((Repo) -> Unit)?
) : ListAdapter<Repo, RepoViewHolder>(REPO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding = LayoutItemRepoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RepoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val repo = getItem(position)
        holder.binding.root.setOnClickListener {
            callback?.invoke(repo)
        }
        holder.bind(repo)
    }

    companion object {

        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem == newItem
            }
        }
    }
}