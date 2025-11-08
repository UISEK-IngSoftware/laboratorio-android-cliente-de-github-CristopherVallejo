package ec.edu.uisek.githubclient.models
import com.google.gson.annotations.SerializedName


data class RepoOwner(
    val id: Long,
    val login: String,
    @SerializedName(value="avatar_url")
    val avatarUrl: String
)
