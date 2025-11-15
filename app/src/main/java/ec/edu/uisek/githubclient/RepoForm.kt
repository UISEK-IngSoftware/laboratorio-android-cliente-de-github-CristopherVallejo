package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoForm : AppCompatActivity() {

    private lateinit var repoFormBinding: ActivityRepoFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repoFormBinding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(repoFormBinding.root)
        repoFormBinding.cancelButton.setOnClickListener { finish() }
        repoFormBinding.saveButton.setOnClickListener { createRepo() }
    }

    private fun validateForm(): Boolean {
        val repoName = repoFormBinding.repoNameInput.text.toString()

        if (repoName.isEmpty()) {
            repoFormBinding.repoNameInput.error = "El nombre del repositorio es requerido"
            return false
        }
        if (repoName.contains(" ")) {
            repoFormBinding.repoNameInput.error = "El nombre del repositorio no puede contener espacios"
            return false
        }
        return true
    }

    private fun createRepo() {
        if (!validateForm()) {
            return
        }
        val repoName = repoFormBinding.repoNameInput.text.toString()
        val repoDescription = repoFormBinding.repoDescriptionInput.text.toString()

        val repoRequest = RepoRequest(
            name = repoName,
            description = repoDescription
        )

        val apiService = RetrofitClient.gitHubApiService
        val call = apiService.addRepository(repoRequest)

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    Log.e("RepoForm", "El Repositorio  ${repoName} fue creado exitosamente")
                    showMessage("El Repositorio  ${repoName} fue creado exitosamente")
                    finish()
                } else {
                    val errMsg = when (response.code()) {
                        401 -> "Error de autenticación"
                        403 -> "Error de autorización"
                        404 -> "Error de recurso no encontrado"
                        else -> "Error desconocido: ${response.code()} ${response.message()}"
                    }
                    Log.e("RepoForm", errMsg)
                    showMessage(errMsg)
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                Log.e("RepoForm", "Error de red: ${t.message}")
                showMessage("Error de red: ${t.message}")
            }
        })
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}