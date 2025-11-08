package ec.edu.uisek.githubclient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding

class RepoItemFragment : Fragment() {

    private var _binding: FragmentRepoItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepoItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.repoName.text = "Mi repositorio de Github"
        binding.repoDescription.text = "Este es un repositorio de prueba"
        binding.repoLang.text = "Kotlin"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
