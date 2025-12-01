package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), RepositoryActionListener {

    // ... (variables y métodos setup, onResume, setupRecyclerView) ...

    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: RepoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.newRepoFab.setOnClickListener {
            displayRepoForm(isEditMode = false, repo = null)
        }
    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
        fetchRepositories()
    }

    private  fun setupRecyclerView(){

        reposAdapter = RepoAdapter(this)
        binding.reposRecyclerView.adapter = reposAdapter
    }

    private fun fetchRepositories(){
        val apiService = RetrofitClient.gitHubApiService
        val call = apiService.getRepos()

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null) {
                        reposAdapter.UpdateRepositories(repos)
                    }
                } else {

                    val errMsg = when (response.code()) {
                        // 🚨 CLAVE: Si hay un error 401 (No autorizado), regresamos al Login
                        401 -> {
                            handleUnauthorized()
                            "Error 401: Token inválido o no autorizado. Vuelve a iniciar sesión."
                        }
                        403 -> "Error 403: Acceso prohibido (Falta de permisos)."
                        404 -> "Error 404: Recurso no encontrado."
                        else -> "Error ${response.code()}: ${response.message()}. ${response.errorBody()?.string()}"
                    }
                    Log.e("MainActivity", errMsg)
                    showMessage(errMsg)
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                val errorMessage = "Error de conexión: ${t.message}"
                Log.e("MainActivity", errorMessage, t)
                showMessage(errorMessage)
            }
        })
    }

    // 🚨 Nuevo método para manejar la redirección al login
    private fun handleUnauthorized() {
        showMessage("Sesión expirada o token inválido. Redirigiendo al inicio de sesión.")
        val intent = Intent(this, LoginActivity::class.java).apply {
            // Limpia la pila de actividades para que el usuario no pueda volver a MainActivity sin token
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }


    private fun displayRepoForm(isEditMode: Boolean, repo: Repo?) {
        // ... (resto del método displayRepoForm sin cambios) ...
        val intent = Intent(this, RepoForm::class.java).apply {
            if (isEditMode && repo != null) {
                putExtra("MODE", "EDIT")
                putExtra("REPO_NAME", repo.name)
                putExtra("REPO_OWNER_LOGIN", repo.owner.login)
                putExtra("REPO_DESCRIPTION", repo.description)
            } else {
                putExtra("MODE", "CREATE")
            }
        }
        startActivity(intent)
    }


    override fun onEdit(repo: Repo) {
        displayRepoForm(isEditMode = true, repo)
    }


    override fun onDelete(owner: String, repoName: String) {
        val call = RetrofitClient.gitHubApiService.deleteRepository(owner, repoName)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio '$repoName' eliminado correctamente.")
                    fetchRepositories() // Recarga la lista
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errMsg = when (response.code()) {
                        401 -> {
                            handleUnauthorized()
                            "Error 401: Token inválido al eliminar. Reintentando..."
                        }
                        else -> "Error al eliminar: ${response.code()}. ${errorBody}"
                    }
                    Log.e("MainActivity", errMsg)
                    showMessage(errMsg)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                val errorMessage = "Fallo de red al intentar eliminar: ${t.message}"
                Log.e("MainActivity", errorMessage, t)
                showMessage(errorMessage)
            }
        })
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}