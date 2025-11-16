package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import retrofit2.Call
import retrofit2.http.*

interface GitHubApiService {


    @GET("users/repos")
    fun getRepos(
        @Query("sort") sort: String = "created",
        @Query("direction") direction: String = "desc"
    ): Call<List<Repo>>


    @POST("users/repos")
    fun addRepository(
        @Body repoRequest: RepoRequest
    ): Call<Repo>


    @PATCH("repos/{owner}/{repo}")
    fun editRepository(
        @Path("owner") owner: String,
        @Path("repo") repoName: String,
        @Body repoRequest: RepoRequest
    ): Call<Repo>


    @DELETE("repos/{owner}/{repo}")
    fun deleteRepository(
        @Path("owner") owner: String,
        @Path("repo") repoName: String
    ): Call<Void>
}