package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import retrofit2.http.GET
import retrofit2.Call

interface GitHubApiService {
    @GET("users/repos")
    fun getRepos():Call<List<Repo>>
}
