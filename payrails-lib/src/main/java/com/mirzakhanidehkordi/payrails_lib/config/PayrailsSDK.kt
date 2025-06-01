package com.mirzakhanidehkordi.payrails_lib.config

import com.mirzakhanidehkordi.payrails_lib.api.PayrailsApi
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
 * Singleton object for initializing and providing access to the Payrails SDK client.
 * Handles configuration of base URLs and API client setup, including token injection.
 */
object PayrailsSDK {
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

        // Create a Retrofit instance without the auth interceptor to get the initial token
        val tempRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpClient()) // Use a basic OkHttpClient for token fetching
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        tokenManager = TokenManager(tempRetrofit.create(PayrailsApi::class.java), clientId)

        // Create an OkHttpClient with an interceptor to add the authorization header
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                // This call is blocking, consider moving token acquisition out of interceptor
                // for a more reactive approach in a real-world scenario (e.g., using an authenticator)
                val token = runBlocking { tokenManager.getToken() }
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .build()

        // Build the main Retrofit instance with the authenticated OkHttpClient
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(PayrailsApi::class.java)
        client = PayrailsClient(api, tokenManager)
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