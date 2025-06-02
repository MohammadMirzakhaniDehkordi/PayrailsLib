package com.mirzakhanidehkordi.payrails_lib.auth.test

import com.mirzakhanidehkordi.payrails_lib.api.PayrailsApi
import com.mirzakhanidehkordi.payrails_lib.api.TokenResponse
import com.mirzakhanidehkordi.payrails_lib.auth.TokenManager
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class TokenManagerUnitTests {

    private lateinit var mockPayrailsApi: PayrailsApi
    private lateinit var tokenManager: TokenManager
    private val clientId = "test_client_id"

    @BeforeEach
    fun setUp() {
        // Create a mock instance of PayrailsApi
        mockPayrailsApi = mockk()
        // Initialize TokenManager with the mock API
        tokenManager = TokenManager(mockPayrailsApi, clientId)
    }

    @Test
    fun `getOrRefreshToken should fetch and return token when no token is cached`() = runTest {
        // Arrange: Define the behavior of the mock API when getToken is called
        val expectedToken = "new_access_token_123"
        val tokenResponse = TokenResponse(expectedToken, "Bearer", 3600)
        coEvery { mockPayrailsApi.getToken(clientId) } returns tokenResponse

        // Act: Call the method under test
        val token = tokenManager.getOrRefreshToken()

        // Assert: Verify the token was returned correctly
        assertEquals(expectedToken, token)
        // Verify that getCurrentToken also returns the newly fetched token
        assertEquals(expectedToken, tokenManager.getCurrentToken())
    }

    @Test
    fun `getOrRefreshToken should return cached token if not expired`() = runTest {
        // Arrange: First, fetch a token to cache it
        val initialToken = "initial_access_token_abc"
        val initialTokenResponse = TokenResponse(initialToken, "Bearer", 3600)
        coEvery { mockPayrailsApi.getToken(clientId) } returns initialTokenResponse
        tokenManager.getOrRefreshToken() // Cache the token

        // Arrange: Now, ensure that getToken is NOT called again if the token is fresh
        coEvery { mockPayrailsApi.getToken(clientId) } throws IllegalStateException("Should not call getToken again")

        // Act: Call the method again
        val token = tokenManager.getOrRefreshToken()

        // Assert: Verify the initial token was returned, and API was not called again
        assertEquals(initialToken, token)
    }

    @Test
    fun `getOrRefreshToken should refresh token if expired`() = runTest {
        // Arrange: Set up a token that expires quickly
        val expiredToken = "expired_token_xyz"
        val expiredTokenResponse = TokenResponse(expiredToken, "Bearer", 1) // Expires in 1 second
        coEvery { mockPayrailsApi.getToken(clientId) } returns expiredTokenResponse
        tokenManager.getOrRefreshToken() // Cache the token

        // Advance time past the token's expiration buffer (60 seconds + 1 second expiry)
        testScheduler.advanceTimeBy(61.seconds)

        // Arrange: Define the behavior for the refresh call
        val newToken = "refreshed_token_456"
        val newTokenResponse = TokenResponse(newToken, "Bearer", 3600)
        // Ensure that the next call to getToken returns the new token
        coEvery { mockPayrailsApi.getToken(clientId) } returns newTokenResponse

        // Act: Call the method, expecting it to refresh
        val token = tokenManager.getOrRefreshToken()

        // Assert: Verify the new token was returned
        assertEquals(newToken, token)
        // Verify that getCurrentToken also returns the refreshed token
        assertEquals(newToken, tokenManager.getCurrentToken())
    }

    @Test
    fun `getOrRefreshToken should throw exception if token fetching fails`() = runTest {
        // Arrange: Make the mock API throw an exception when getToken is called
        val errorMessage = "Network error"
        coEvery { mockPayrailsApi.getToken(clientId) } throws Exception(errorMessage)

        // Act & Assert: Verify that the exception is re-thrown
        val exception = assertThrows(Exception::class.java) {
            runTest { tokenManager.getOrRefreshToken() }
        }
        assertEquals(errorMessage, exception.message)
        // Verify that no token is cached
        assertNull(tokenManager.getCurrentToken())
    }

    @Test
    fun `getCurrentToken should return null if no token is cached`() {
        // Act & Assert
        assertNull(tokenManager.getCurrentToken())
    }

    @Test
    fun `getCurrentToken should return null if token is expired`() = runTest {
        // Arrange: Cache an expired token
        val expiredToken = "expired_token_abc"
        val expiredTokenResponse = TokenResponse(expiredToken, "Bearer", 1) // Expires in 1 second
        coEvery { mockPayrailsApi.getToken(clientId) } returns expiredTokenResponse
        tokenManager.getOrRefreshToken() // Cache the token

        // Advance time past expiration
        testScheduler.advanceTimeBy(61.seconds)

        // Act & Assert
        assertNull(tokenManager.getCurrentToken())
    }
}