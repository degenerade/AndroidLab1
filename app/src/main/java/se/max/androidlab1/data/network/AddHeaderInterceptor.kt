package se.max.androidlab1.data.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import se.max.androidlab1.BuildConfig

class AddHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val modifiedRequest: Request = request
            .newBuilder()
            .addHeader("Authorization", "Bearer ${BuildConfig.GITHUB_TOKEN}")
            .addHeader("Accept", "application/vnd.github+json")
            .build()
        return chain.proceed(modifiedRequest)
    }
}
