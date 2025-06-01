package com.mirzakhanidehkordi.payrails_lib.auth

import com.mirzakhanidehkordi.payrails_lib.api.PayrailsApi
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Manages the acquisition and caching of authentication tokens for the Payrails API.
 * Ensures that only one token request is in progress at a time using a [Mutex].
 *
 * @param api The [PayrailsApi] instance used to fetch tokens.
 * @param clientId The client ID required for token authentication.
 */
class TokenManager(private val api: PayrailsApi, private val clientId: String) {
    private var token: String? = null
    private val mutex = Mutex()

    /**
     * Retrieves a valid access token. If no token is available or if it's expired
     * (though expiration isn't explicitly handled here, it would be added in a real app),
     * a new token is fetched. Uses a mutex to prevent multiple concurrent token fetches.
     *
     * @return The access token string.
     */
    suspend fun getToken(): String = mutex.withLock {

        if (token == null) {
            val response = api.getToken(clientId)
            token = response.accessToken
        }
        token!!
    }
}