package ec.edu.uisek.githubclient.services

import android.util.Log
import ec.edu.uisek.githubclient.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto singleton que configura y proporciona la instancia de Retrofit
 * para conectarse a la API de GitHub
 */
object RetrofitClient {

    private const val TAG = "RetrofitClient"

    // URL base de la API de GitHub
    private const val BASE_URL = "https://api.github.com/"

    /**
     * Interceptor que agrega el token de autenticación a todas las peticiones
     * (Usando "Bearer" en lugar de "token" y gestionando la ausencia de token)
     */
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = BuildConfig.GITHUB_API_TOKEN

        // Si el token está configurado, agregarlo al header Authorization
        val newRequest = if (token.isNotEmpty()) {
            originalRequest.newBuilder()
                //  CAMBIO: Usamos "Bearer" para el token
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build()
        } else {
            // Si no hay token, solo agregar el header Accept y loguear un aviso
            Log.w(TAG, "🚨 Token de Github NO CONFIGURADO ")
            originalRequest.newBuilder()
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build()
        }

        chain.proceed(newRequest)
    }

    /**
     * Interceptor para logging de peticiones y respuestas (útil para depuración)
     * 🚨 CAMBIO: Se ajusta el nivel de logging basado en BuildConfig.DEBUG
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY // Nivel detallado en debug
        } else {
            HttpLoggingInterceptor.Level.NONE // Ningún log en producción (release)
        }
    }

    /**
     * Cliente HTTP configurado con los interceptores necesarios
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Instancia de Retrofit configurada con la URL base, el cliente HTTP
     * y el convertidor Gson para serializar/deserializar JSON
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Instancia del servicio de la API de GitHub
     * Se crea de forma lazy (solo cuando se accede por primera vez)
     */
    val gitHubApiService: GitHubApiService by lazy {
        retrofit.create(GitHubApiService::class.java)
    }
}