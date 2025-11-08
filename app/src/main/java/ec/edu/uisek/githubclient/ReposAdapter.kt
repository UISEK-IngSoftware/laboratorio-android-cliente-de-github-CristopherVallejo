package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding
import ec.edu.uisek.githubclient.models.Repo


class RepoViewHolder(private val binding: FragmentRepoItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(repo: Repo) {
            binding.repoName.text = repo.name
            binding.repoDescription.text = repo.description ?: "No existe descripción en el repositorio"
            binding.repoLang.text = repo.language ?: "No existe lenguaje en el repositorio"
            Glide.with(binding.root.context)
                .load(repo.owner.avatarUrl)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .circleCrop()
                .into(binding.repoOwnerImage)
        }
    }
class  RepoAdapter : RecyclerView.Adapter<RepoViewHolder>() {

    private var repositories: List<Repo> = emptyList()

    fun submitList(repos: List<Repo>) {
        this.repositories = repos
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = repositories.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding = FragmentRepoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RepoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(repositories[position])
    }

    fun UpdateRepositories(newRepositories: List<Repo>) {
        repositories = newRepositories
        notifyDataSetChanged()
    }
}