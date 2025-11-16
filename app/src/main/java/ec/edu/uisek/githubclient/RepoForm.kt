package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoForm : AppCompatActivity() {

    private lateinit var repoFormBinding: ActivityRepoFormBinding
    private var isEditMode = false
    private var originalRepoName: String? = null
    private var repoOwnerLogin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repoFormBinding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(repoFormBinding.root)

        setupMode()

        // IDs ADAPTADOS: cancel_button y save_button
        repoFormBinding.cancelButton.setOnClickListener { finish() }
        repoFormBinding.saveButton.setOnClickListener {
            // Lógica para decidir si crear o actualizar
            if (isEditMode) updateRepo() else createRepo()
        }
    }

    private fun setupMode() {
        val mode = intent.getStringExtra("MODE")
        isEditMode = mode == "EDIT"

        if (isEditMode) {
            title = "Editar Repositorio"
            originalRepoName = intent.getStringExtra("REPO_NAME")
            repoOwnerLogin = intent.getStringExtra("REPO_OWNER_LOGIN")
            val description = intent.getStringExtra("REPO_DESCRIPTION")

            // IDs ADAPTADOS: repo_name_input y repo_description_input
            repoFormBinding.repoNameInput.setText(originalRepoName)
            repoFormBinding.repoDescriptionInput.setText(description)
            // No se puede cambiar el nombre del repo en edición
            repoFormBinding.repoNameInput.isEnabled = false
        } else {
            title = "Crear Nuevo Repositorio"
            repoFormBinding.repoNameInput.isEnabled = true
        }
    }

    private fun validateForm(): Boolean {
        // IDs ADAPTADOS: repo_name_input
        val repoName = repoFormBinding.repoNameInput.text.toString()

        if (repoName.isEmpty()) {
            repoFormBinding.repoNameInput.error = "El nombre es obligatorio"
            return false
        }
        // Validación de tu segundo código
        if (repoName.contains(" ")) {
            repoFormBinding.repoNameInput.error = "El nombre del repositorio no puede contener espacios"
            return false
        }
        return true
    }

    private fun createRepo() {
        if (!validateForm()) return

        // IDs ADAPTADOS
        val repoName = repoFormBinding.repoNameInput.text.toString()
        val repoDescription = repoFormBinding.repoDescriptionInput.text.toString()

        val repoRequest = RepoRequest(
            name = repoName,
            description = repoDescription
        )

        RetrofitClient.gitHubApiService.addRepository(repoRequest).enqueue(handleApiResponse("creado", repoName))
    }

    private fun updateRepo() {
        if (!validateForm()) return

        // IDs ADAPTADOS
        val repoDescription = repoFormBinding.repoDescriptionInput.text.toString()

        val repoRequest = RepoRequest(
            name = originalRepoName!!, // Usamos el nombre original como path y en el body
            description = repoDescription
        )

        RetrofitClient.gitHubApiService.editRepository(
            owner = repoOwnerLogin!!,
            repoName = originalRepoName!!,
            repoRequest = repoRequest
        ).enqueue(handleApiResponse("actualizado", originalRepoName!!))
    }

    // Función de ayuda para manejar la respuesta de la API (POST y PATCH)
    private fun handleApiResponse(action: String, repoName: String): Callback<Repo> {
        return object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    // Mensaje de éxito de tu segundo código
                    val successMsg = "El Repositorio '$repoName' fue $action exitosamente."
                    Log.e("RepoForm", successMsg)
                    showMessage(successMsg)
                    finish()
                } else {
                    // Manejo de error de tu segundo código
                    handleError(response)
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                // Manejo de fallo de red de tu segundo código
                val errorMessage = "Error de red: ${t.message}"
                Log.e("RepoForm", errorMessage)
                showMessage(errorMessage)
            }
        }
    }

    private fun handleError(response: Response<*>) {
        val errMsg = when (response.code()) {
            401 -> "Error de autenticación: Token inválido."
            403 -> "Error de autorización: Permisos insuficientes."
            404 -> "Error de recurso no encontrado."
            else -> "Error desconocido: ${response.code()} ${response.message()}"
        }
        val detailedError = response.errorBody()?.string()
        Log.e("RepoForm", "$errMsg - Detalle: $detailedError")
        showMessage(errMsg)
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }
}