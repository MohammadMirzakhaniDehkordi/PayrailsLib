package com.mirzakhanidehkordi.payrails_lib.client.test

import com.mirzakhanidehkordi.payrails_lib.api.*
import com.mirzakhanidehkordi.payrails_lib.auth.CardDetails
import com.mirzakhanidehkordi.payrails_lib.auth.TokenManager
import com.mirzakhanidehkordi.payrails_lib.auth.TokenizedCard
import com.mirzakhanidehkordi.payrails_lib.auth.tokenizeCard // Import the extension function
import com.mirzakhanidehkordi.payrails_lib.client.PayrailsClient
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

@OptIn(ExperimentalCoroutinesApi::class)
class PayrailsClientUnitTests {

    private lateinit var mockPayrailsApi: PayrailsApi
    private lateinit var mockTokenManager: TokenManager
    private lateinit var payrailsClient: PayrailsClient

    @BeforeEach
    fun setUp() {
        mockPayrailsApi = mockk()
        mockTokenManager = mockk()
        payrailsClient = PayrailsClient(mockPayrailsApi, mockTokenManager)
    }

    @Test
    fun `getPayment should return Success with Payment on API success`() = runTest {
        // Arrange
        val paymentId = "payment_123"
        val expectedPayment = Payment(
            id = paymentId,
            status = "COMPLETED",
            history = listOf(
                PaymentOperation("op1", "CAPTURE", "SUCCESS", "200", emptyList())
            )
        )
        coEvery { mockPayrailsApi.getPayment(paymentId) } returns expectedPayment

        // Act
        val result = payrailsClient.getPayment(paymentId)

        // Assert
        assertTrue(result is ApiResult.Success)
        assertEquals(expectedPayment, (result as ApiResult.Success).data)
    }

    @Test
    fun `getPayment should return Error on API failure`() = runTest {
        // Arrange
        val paymentId = "payment_123"
        val expectedException = RuntimeException("Payment not found")
        coEvery { mockPayrailsApi.getPayment(paymentId) } throws expectedException

        // Act
        val result = payrailsClient.getPayment(paymentId)

        // Assert
        assertTrue(result is ApiResult.Error)
        assertEquals(expectedException.message, (result as ApiResult.Error).exception.message)
    }

    @Test
    fun `capturePayment should return Success with CaptureResponse on API success`() = runTest {
        // Arrange
        val paymentId = "payment_456"
        val amount = 100.0
        val currency = "USD"
        val expectedCaptureResponse = CaptureResponse(
            success = true,
            action = "CAPTURE",
            amount = Amount(amount.toString(), currency),
            execution = Execution("exec_1", "ref_1")
        )
        // Mock the API call for capturePayment
        coEvery { mockPayrailsApi.capturePayment(paymentId, any()) } returns expectedCaptureResponse

        // Act
        val result = payrailsClient.capturePayment(paymentId, amount, currency)

        // Assert
        assertTrue(result is ApiResult.Success)
        assertEquals(expectedCaptureResponse, (result as ApiResult.Success).data)
    }

    @Test
    fun `capturePayment should return Error on API failure`() = runTest {
        // Arrange
        val paymentId = "payment_456"
        val amount = 100.0
        val currency = "USD"
        val expectedException = Exception("Capture failed")
        coEvery { mockPayrailsApi.capturePayment(paymentId, any()) } throws expectedException

        // Act
        val result = payrailsClient.capturePayment(paymentId, amount, currency)

        // Assert
        assertTrue(result is ApiResult.Error)
        assertEquals(expectedException.message, (result as ApiResult.Error).exception.message)
    }

    @Test
    fun `tokenizeCard should return Success with TokenizedCard on API success`() = runTest {
        // Arrange
        val cardDetails = CardDetails("1234567890123456", "12", "2025", "123")
        val expectedTokenizedCard = TokenizedCard("tok_mock", "3456", "12", "2025")
        // Mock the extension function call
        coEvery { mockPayrailsApi.tokenizeCard(cardDetails) } returns expectedTokenizedCard

        // Act
        val result = payrailsClient.tokenizeCard(cardDetails)

        // Assert
        assertTrue(result is ApiResult.Success)
        assertEquals(expectedTokenizedCard, (result as ApiResult.Success).data)
    }

    @Test
    fun `tokenizeCard should return Error on API failure`() = runTest {
        // Arrange
        val cardDetails = CardDetails("1234567890123456", "12", "2025", "123")
        val expectedException = IllegalStateException("Tokenization service unavailable")
        coEvery { mockPayrailsApi.tokenizeCard(cardDetails) } throws expectedException

        // Act
        val result = payrailsClient.tokenizeCard(cardDetails)

        // Assert
        assertTrue(result is ApiResult.Error)
        assertEquals(expectedException.message, (result as ApiResult.Error).exception.message)
    }
}
