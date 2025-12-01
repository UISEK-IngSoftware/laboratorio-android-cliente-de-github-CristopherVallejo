package ec.edu.uisek.githubclient

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val PREFS_NAME = "GitHubClientPrefs"
    private val TOKEN_KEY = "AuthToken"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Verificar sesión persistente al iniciar
        if (isLoggedIn()) {
            launchMainActivity()
            return
        }

        binding.loginButton.setOnClickListener {
            // En la práctica de GitHub, la "contraseña" es el Token.
            val token = binding.passwordInput.text.toString().trim()

            if (token.isNotEmpty()) {
                saveToken(token)
                launchMainActivity()
            } else {
                Toast.makeText(this, "El token (contraseña) es obligatorio.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isLoggedIn(): Boolean {
        // Obtenemos el token guardado. Si no existe, devuelve una cadena vacía.
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(TOKEN_KEY, "")!!.isNotEmpty()
    }

    private fun saveToken(token: String) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(TOKEN_KEY, token)
            .apply() // Guarda el token de forma asíncrona
    }

    // Función para navegar a la pantalla principal
    private fun launchMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish() // Finaliza esta actividad para que el usuario no pueda volver con el botón "atrás"
    }
}