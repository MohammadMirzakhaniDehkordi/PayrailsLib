package com.mirzakhanidehkordi.payrails_lib.config

import com.mirzakhanidehkordi.payrails_lib.api.PayrailsApi
import com.mirzakhanidehkordi.payrails_lib.auth.AuthTokenAuthenticator
import com.mirzakhanidehkordi.payrails_lib.auth.TokenManager
import com.mirzakhanidehkordi.payrails_lib.client.PayrailsClient
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Defines the available environments for the Payrails library..
 */
enum class Environment {
    STAGING, PRODUCTION
}

/**
 * Singleton object for initializing and providing access to the Payrails Lib client.
 * Handles configuration of base URLs and API client setup, including token injection.
 */
object PayrailsLib {
    private lateinit var client: PayrailsClient
    private lateinit var tokenManager: TokenManager
    private var baseUrl: String = "https://payrails-api.staging.payrails.io/" // Default staging URL

    /**
     * Initializes the Payrails SDK with the provided client ID and environment.
     * This method must be called before [getClient] is invoked.
     *
     * @param clientId The unique client identifier for authentication.
     * @param environment The target environment (STAGING or PRODUCTION).
     */
    fun initialize(clientId: String, environment: Environment = Environment.STAGING) {
        baseUrl = when (environment) {
            Environment.STAGING -> "https://payrails-api.staging.payrails.io/"
            Environment.PRODUCTION -> "https://payrails-api.payrails.io/"
        }

        // Initialize a basic Retrofit instance for token fetching (no auth interceptor needed here)
        val authRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpClient.Builder().build()) // Basic client for auth requests
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        tokenManager = TokenManager(authRetrofit.create(PayrailsApi::class.java), clientId)

        // Create an OkHttpClient with an Authenticator for token refresh
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                // This interceptor just adds the *current* token if available.
                // The Authenticator handles refreshing when a 401 occurs.
                val originalRequest = chain.request()
                val token = tokenManager.getCurrentToken() // Get currently cached token without blocking
                if (token != null && originalRequest.header("Authorization") == null) {
                    val authorizedRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(authorizedRequest)
                } else {
                    chain.proceed(originalRequest)
                }
            }
            .authenticator(AuthTokenAuthenticator(tokenManager)) // Custom Authenticator
            .build()

        // Build the main Retrofit instance with the authenticated OkHttpClient
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(PayrailsApi::class.java)
        client = PayrailsClient(api, tokenManager) // tokenManager might not be needed in PayrailsClient directly now
    }


    /**
     * Provides the initialized [PayrailsClient] instance.
     * Must be called after [initialize].
     * @throws IllegalStateException if [initialize] has not been called.
     * @return The [PayrailsClient] instance ready for use.
     */
    fun getClient(): PayrailsClient {
        if (!::client.isInitialized) {
            throw IllegalStateException("PayrailsSDK must be initialized before calling getClient().")
        }
        return client
    }
}