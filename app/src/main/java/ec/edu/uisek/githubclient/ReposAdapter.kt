package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ec.edu.uisek.githubclient.databinding.FragmentRepoltemBinding


class RepoViewHolder(private val binding: FragmentRepoltemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.repoOwner.setImageResource(R.mipmap.ic_launcher)
            binding.repoName.text = "Repositorio #${position + 1}"
            binding.repoDescription.text = "Esta es la descripción del elemento número ${position + 1} en la lista."
            binding.repoLang.text = if (position % 2 == 0) "Kotlin" else "Java"
        }
    }
class  RepoAdapter : RecyclerView.Adapter<RepoViewHolder>() {

    override fun getItemCount(): Int = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val binding = FragmentRepoltemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RepoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(position)
    }
}
