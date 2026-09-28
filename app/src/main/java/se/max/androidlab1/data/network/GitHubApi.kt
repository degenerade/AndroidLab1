package se.max.androidlab1.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import se.max.androidlab1.data.model.Comment
import se.max.androidlab1.data.model.GetResponse
import se.max.androidlab1.data.model.PrState
import se.max.androidlab1.data.model.PullRequest
import se.max.androidlab1.data.model.PutBody

interface GitHubApi {
    @GET("repos/{owner}/{repo}/pulls")
    suspend fun getPullRequest(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ) : List<PullRequest>

    @GET("repos/{owner}/{repo}/issues/{pull_number}/comments")
    suspend fun getComments(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullNumber: Int
    ) : List<Comment>

    @PATCH("repos/{owner}/{repo}/pulls/{pull_number}")
    suspend fun updatePullRequest(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("pull_number") pullNumber: Int,
        @Body body: PrState
    )

    @GET("repos/{owner}/{repo}/contents/{path}")
    suspend fun getContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String
    ) : GetResponse

    @PUT("repos/{owner}/{repo}/contents/{path}")
    suspend fun putContent(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("path") path: String,
        @Body body: PutBody
    )
}