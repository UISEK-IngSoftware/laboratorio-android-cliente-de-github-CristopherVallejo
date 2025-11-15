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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: RepoAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.newRepoFab.setOnClickListener {
            displayNewRepoForm()
        }


    }

    override fun onResume() {
        super.onResume()
        setupRecyclerView()
        fetchRepositories()
    }

    private  fun setupRecyclerView(){
        reposAdapter = RepoAdapter()
        binding.reposRecyclerView.adapter = reposAdapter
    }
    private fun fetchRepositories(){
        val apiService = RetrofitClient.gitHubApiService
        val call = apiService.getRepos()
        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null && repos.isNotEmpty()) {
                        reposAdapter.UpdateRepositories(repos)
                    }
                } else {
                    val errMsg = when (response.code()) {
                        401 -> "Error de autenticación"
                        403 -> "Error de autorización"
                        404 -> "Error de recurso no encontrado"
                        else -> "Error desconocido: ${response.code()} ${response.message()}"
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

            private fun showMessage(msg: String) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }

    private  fun displayNewRepoForm(){
        val intent = Intent(this, RepoForm::class.java)
        startActivity(intent)
    }

}