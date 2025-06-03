package com.mirzakhanidehkordi.payrails_lib.auth.test

import com.mirzakhanidehkordi.payrails_lib.api.ApiResult
import com.mirzakhanidehkordi.payrails_lib.api.PayrailsApi
import com.mirzakhanidehkordi.payrails_lib.auth.CardDetails
import com.mirzakhanidehkordi.payrails_lib.auth.CardTokenizer
import com.mirzakhanidehkordi.payrails_lib.auth.TokenizedCard
import com.mirzakhanidehkordi.payrails_lib.auth.tokenizeCard // Import the extension function
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

@OptIn(ExperimentalCoroutinesApi::class)
class CardTokenizerUnitTests {

    private lateinit var mockPayrailsApi: PayrailsApi
    private lateinit var cardTokenizer: CardTokenizer

    @BeforeEach
    fun setUp() {
        mockPayrailsApi = mockk()
        cardTokenizer = CardTokenizer(mockPayrailsApi)
        mockkStatic("com.mirzakhanidehkordi.payrails_lib.auth.CardTokenizerKt")
    }

    @AfterEach
    fun tearDown() {
        unmockkStatic("com.mirzakhanidehkordi.payrails_lib.auth.CardTokenizerKt")
    }

    @Test
    fun `tokenizeCard should return Success with TokenizedCard for valid details`() = runTest {
        // Arrange
        val validCardDetails = CardDetails("4111222233334444", "12", "2025", "123")
        val expectedTokenizedCard = TokenizedCard("tok_test", "4444", "12", "2025")

        // Mock the behavior of the PayrailsApi extension function
        coEvery { mockPayrailsApi.tokenizeCard(validCardDetails) } returns expectedTokenizedCard

        // Act
        val result = cardTokenizer.tokenizeCard(validCardDetails)

        // Assert
        assertTrue(result is ApiResult.Success)
        assertEquals(expectedTokenizedCard, (result as ApiResult.Success).data)
    }

    @Test
    fun `tokenizeCard should return Error for invalid card number length`() = runTest {
        // Arrange
        val invalidCardDetails = CardDetails("123", "12", "2025", "123") // Too short
        // No need to mock API call as validation happens before it

        // Act
        val result = cardTokenizer.tokenizeCard(invalidCardDetails)

        // Assert
        assertTrue(result is ApiResult.Error)
        assertTrue((result as ApiResult.Error).exception is IllegalArgumentException)
        assertEquals("Invalid card details", result.exception.message)
    }

    @Test
    fun `tokenizeCard should return Error for invalid expiry month`() = runTest {
        // Arrange
        val invalidCardDetails = CardDetails("4111222233334444", "13", "2025", "123") // Invalid month
        // No need to mock API call

        // Act
        val result = cardTokenizer.tokenizeCard(invalidCardDetails)

        // Assert
        assertTrue(result is ApiResult.Error)
        assertTrue((result as ApiResult.Error).exception is IllegalArgumentException)
        assertEquals("Invalid card details", result.exception.message)
    }

    @Test
    fun `tokenizeCard should return Error for invalid CVV length`() = runTest {
        // Arrange
        val invalidCardDetails = CardDetails("4111222233334444", "12", "2025", "1") // Too short
        // No need to mock API call

        // Act
        val result = cardTokenizer.tokenizeCard(invalidCardDetails)

        // Assert
        assertTrue(result is ApiResult.Error)
        assertTrue((result as ApiResult.Error).exception is IllegalArgumentException)
        assertEquals("Invalid card details", result.exception.message)
    }

    @Test
    fun `tokenizeCard should return Error if API call fails`() = runTest {
        // Arrange
        val validCardDetails = CardDetails("4111222233334444", "12", "2025", "123")
        val expectedException = RuntimeException("API tokenization failed")
        coEvery { mockPayrailsApi.tokenizeCard(validCardDetails) } throws expectedException

        // Act
        val result = cardTokenizer.tokenizeCard(validCardDetails)

        // Assert
        assertTrue(result is ApiResult.Error)
        assertEquals(expectedException.message, (result as ApiResult.Error).exception.message)
    }
}
