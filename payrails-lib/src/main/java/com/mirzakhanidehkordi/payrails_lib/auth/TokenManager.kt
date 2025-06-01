package com.mirzakhanidehkordi.payrails_lib.auth

import com.mirzakhanidehkordi.payrails_lib.api.PayrailsApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource.Monotonic.markNow

/**
 * Manages the acquisition and caching of authentication tokens for the Payrails API.
 * Ensures that only one token request is in progress at a time using a [Mutex].
 *
 * @param api The [PayrailsApi] instance used to fetch tokens.
 * @param clientId The client ID required for token authentication.
 */
class TokenManager(private val api: PayrailsApi, private val clientId: String) {
    private var tokenInfo: TokenInfo? = null
    private val mutex = Mutex()

    // Data class to hold token and its expiration information
    private data class TokenInfo(
        val accessToken: String,
        val tokenType: String,
        val expiresIn: Int, // seconds
        val createdAt: TimeMark = markNow() // Timestamp when the token was acquired
    ) {
        fun isExpired(): Boolean {
            // Add a buffer to account for network latency and processing time
            val expirationBuffer = 60.seconds // e.g., refresh 60 seconds before actual expiry
            return createdAt.elapsedNow() >= (expiresIn.seconds - expirationBuffer)
        }
    }
    /**
     * Retrieves the currently cached token without attempting to refresh it.
     * Useful for interceptors that add the token if it's already present.
     * @return The access token string, or null if not available or expired.
     */
    fun getCurrentToken(): String? {
        return tokenInfo?.accessToken?.takeUnless { tokenInfo?.isExpired() == true }
    }

    /**
     * Retrieves a valid access token, refreshing it if necessary.
     * Uses a mutex to prevent multiple concurrent token fetches.
     *
     * @return The access token string.
     * @throws Exception if token acquisition fails.
     */
    suspend fun getOrRefreshToken(): String = mutex.withLock {
        if (tokenInfo == null || tokenInfo!!.isExpired()) {
            try {
                val response = api.getToken(clientId)
                tokenInfo = TokenInfo(
                    accessToken = response.accessToken,
                    tokenType = response.tokenType,
                    expiresIn = response.expiresIn
                )
            } catch (e: Exception) {
                // Log the error for debugging
                // Log.e("TokenManager", "Failed to acquire or refresh token", e)
                throw e // Re-throw to be handled by the Authenticator or calling code
            }
        }
        tokenInfo!!.accessToken
    }
}


