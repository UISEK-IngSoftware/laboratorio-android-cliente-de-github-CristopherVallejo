package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding
import ec.edu.uisek.githubclient.models.Repo

// Interfaz para comunicar las acciones de los botones a la actividad principal
interface RepositoryActionListener {
    fun onEdit(repo: Repo)
    fun onDelete(owner: String, repoName: String)
}

// El ViewHolder ahora recibe el listener para manejar los clicks
class RepoViewHolder(
    private val binding: FragmentRepoItemBinding,
    private val listener: RepositoryActionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(repo: Repo) {
        binding.repoName.text = repo.name
        // Se usa la descripción de tu segundo ejemplo para el texto de fallback
        binding.repoDescription.text = repo.description ?: "No existe descripción en el repositorio"
        binding.repoLang.text = repo.language ?: "No existe lenguaje en el repositorio"

        // Cargar imagen con Glide
        Glide.with(binding.root.context)
            .load(repo.owner.avatarUrl)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .circleCrop()
            .into(binding.repoOwnerImage)


        binding.ivEdit.setOnClickListener { listener.onEdit(repo) }
        binding.ivDelete.setOnClickListener { listener.onDelete(repo.owner.login, repo.name) }
    }
}


class RepoAdapter(private val listener: RepositoryActionListener) : RecyclerView.Adapter<RepoViewHolder>() {

    private var repositories: List<Repo> = emptyList()


    fun submitList(repos: List<Repo>) {
        this.repositories = repos
        notifyDataSetChanged()
    }


    fun UpdateRepositories(newRepositories: List<Repo>) {
        submitList(newRepositories) // Reutilizamos submitList
    }

    override fun getItemCount(): Int = repositories.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding = FragmentRepoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        // 🚨 REINTEGRACIÓN: Pasamos el listener al ViewHolder 🚨
        return RepoViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(repositories[position])
    }
}