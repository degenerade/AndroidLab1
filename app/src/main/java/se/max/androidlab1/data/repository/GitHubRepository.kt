package se.max.androidlab1.data.repository

import android.util.Base64
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import se.max.androidlab1.data.model.PrComment
import se.max.androidlab1.data.model.PrState
import se.max.androidlab1.data.model.PutBody
import se.max.androidlab1.data.network.AddHeaderInterceptor
import se.max.androidlab1.data.network.GitHubApi

private const val CONFIG_PATH = "house_config.json"

class GitHubRepository {
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .addInterceptor(AddHeaderInterceptor())
            .build()
    }

    private val gitHubApi: GitHubApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GitHubApi::class.java)
    }

    suspend fun getAllOpenComments(owner: String, repo: String) : List<PrComment> {
        val pullRequests = gitHubApi.getPullRequest(owner, repo)
        return pullRequests.flatMap { pr ->
            gitHubApi.getComments(owner, repo, pr.number).map { comment ->
                PrComment(pr.number, comment.body)
            }
        }
    }

    suspend fun closePullRequest(owner: String, repo: String, number: Int) {
        gitHubApi.updatePullRequest(owner, repo, number, PrState("closed"))
    }

    suspend fun applyEcoConfig(owner: String, repo: String) {
        val response = gitHubApi.getContent(owner, repo, CONFIG_PATH)
        val text = String(
            Base64.decode(
                response.content,
                Base64.DEFAULT
            ), Charsets.UTF_8
        )
        val json = JsonParser.parseString(text).asJsonObject

        json.addProperty("target_temperature", 17.0)
        json.addProperty("last_updated_by", "Android-Operator")

        val newText = GsonBuilder()
            .setPrettyPrinting()
            .create()
            .toJson(json)
            .toByteArray(Charsets.UTF_8)
        val encoded = Base64.encodeToString(
            newText,
            Base64.NO_WRAP
        )

        gitHubApi.putContent(owner, repo, CONFIG_PATH, PutBody(
                message = "Set target temperature to 17.0 (Android-Operator)",
                content = encoded,
                sha = response.sha
            )
        )
    }
}