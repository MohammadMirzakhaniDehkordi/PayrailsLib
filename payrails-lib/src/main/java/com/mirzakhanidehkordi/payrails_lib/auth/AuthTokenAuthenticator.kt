package com.mirzakhanidehkordi.payrails_lib.auth

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import kotlinx.coroutines.runBlocking // Note: Authenticator still uses runBlocking, but it's acceptable
// here as it only runs on 401, not on every request.

/**
 * OkHttp Authenticator to handle refreshing expired or invalid access tokens.
 * It's called automatically by OkHttp when a 401 Unauthorized response is received.
 *
 * @param tokenManager The [TokenManager] responsible for getting and refreshing tokens.
 */
class AuthTokenAuthenticator(private val tokenManager: TokenManager) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Only retry if it's an authentication error
        if (response.request.header("Authorization") != null && response.code == 401) {
            // Synchronously refresh the token. runBlocking is acceptable here because
            // authenticators are designed to block the current call until a new token is obtained
            // or the authentication fails.
            val newToken = runBlocking {
                try {
                    tokenManager.getOrRefreshToken()
                } catch (e: Exception) {
                    // Log the error or handle it. Returning null will cause the original
                    // request to fail with a 401.
                    // Log.e("AuthTokenAuthenticator", "Failed to refresh token during authentication", e)
                    null
                }
            }

            return if (newToken != null) {
                response.request.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()
            } else {
                null // Authentication failed, don't retry the request
            }
        }
        return null // Not an authentication issue, or already tried with new token
    }
}