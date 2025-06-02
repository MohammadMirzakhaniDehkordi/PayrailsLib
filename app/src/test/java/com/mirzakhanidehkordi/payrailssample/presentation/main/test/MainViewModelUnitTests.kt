package com.mirzakhanidehkordi.payrailssample.presentation.main.test


import com.mirzakhanidehkordi.payrails_lib.api.*
import com.mirzakhanidehkordi.payrails_lib.auth.CardDetails
import com.mirzakhanidehkordi.payrails_lib.auth.TokenizedCard
import com.mirzakhanidehkordi.payrails_lib.client.PayrailsClient
import com.mirzakhanidehkordi.payrails_lib.config.PayrailsLib
import com.mirzakhanidehkordi.payrailssample.presentation.main.MainUiState
import com.mirzakhanidehkordi.payrailssample.presentation.main.MainViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelUnitTests {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockPayrailsClient: PayrailsClient
    private lateinit var viewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        // Set the TestDispatcher for all coroutines launched in the ViewModel
        Dispatchers.setMain(testDispatcher)

        // Mock PayrailsLib and its getClient() method
        mockkObject(PayrailsLib)
        mockPayrailsClient = mockk()
        coEvery { PayrailsLib.getClient() } returns mockPayrailsClient

        // Initialize the ViewModel
        viewModel = MainViewModel()
    }

    @AfterEach
    fun tearDown() {
        // Reset the Main dispatcher
        Dispatchers.resetMain()
        // Clear all mocks
        unmockkAll()
    }

    @Test
    fun `getPaymentDetails should update uiState with success message on API success`() = runTest {
        // Arrange
        val paymentId = "test_payment_id"
        val expectedPayment = Payment(id = paymentId, status = "COMPLETED", history = emptyList())
        coEvery { mockPayrailsClient.getPayment(paymentId) } returns ApiResult.Success(expectedPayment)

        // Assert initial state
        assertEquals(MainUiState(isLoading = false, resultMessage = "Results will appear here"), viewModel.uiState.value)

        // Act
        viewModel.getPaymentDetails(paymentId)

        // Advance dispatcher to allow coroutines to run
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert final state
        val expectedMessage = "Payment Details Success: $paymentId, Status: COMPLETED"
        assertEquals(MainUiState(isLoading = false, resultMessage = expectedMessage), viewModel.uiState.value)
        coVerify(exactly = 1) { mockPayrailsClient.getPayment(paymentId) }
    }

    @Test
    fun `getPaymentDetails should update uiState with error message on API failure`() = runTest {
        // Arrange
        val paymentId = "test_payment_id"
        val errorMessage = "Payment not found"
        coEvery { mockPayrailsClient.getPayment(paymentId) } returns ApiResult.Error(Exception(errorMessage))

        // Act
        viewModel.getPaymentDetails(paymentId)

        // Advance dispatcher
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val expectedMessage = "Error getting payment details: $errorMessage"
        assertEquals(MainUiState(isLoading = false, resultMessage = expectedMessage), viewModel.uiState.value)
        coVerify(exactly = 1) { mockPayrailsClient.getPayment(paymentId) }
    }

    @Test
    fun `capturePayment should update uiState with success message on API success`() = runTest {
        // Arrange
        val paymentId = "capture_payment_id"
        val amount = 50.0
        val currency = "USD"
        val expectedCaptureResponse = CaptureResponse(
            success = true,
            action = "CAPTURE",
            amount = Amount(amount.toString(), currency),
            execution = Execution("exec_id", "merchant_ref")
        )
        coEvery { mockPayrailsClient.capturePayment(paymentId, amount, currency) } returns ApiResult.Success(expectedCaptureResponse)

        // Act
        viewModel.capturePayment(paymentId, amount, currency)

        // Advance dispatcher
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val expectedMessage = "Capture Success: true"
        assertEquals(MainUiState(isLoading = false, resultMessage = expectedMessage), viewModel.uiState.value)
        coVerify(exactly = 1) { mockPayrailsClient.capturePayment(paymentId, amount, currency) }
    }

    @Test
    fun `capturePayment should update uiState with error message on API failure`() = runTest {
        // Arrange
        val paymentId = "capture_payment_id"
        val amount = 50.0
        val currency = "USD"
        val errorMessage = "Capture failed"
        coEvery { mockPayrailsClient.capturePayment(paymentId, amount, currency) } returns ApiResult.Error(Exception(errorMessage))

        // Act
        viewModel.capturePayment(paymentId, amount, currency)

        // Advance dispatcher
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val expectedMessage = "Error capturing payment: $errorMessage"
        assertEquals(MainUiState(isLoading = false, resultMessage = expectedMessage), viewModel.uiState.value)
        coVerify(exactly = 1) { mockPayrailsClient.capturePayment(paymentId, amount, currency) }
    }

    @Test
    fun `tokenizeCard should update uiState with success message on API success`() = runTest {
        // Arrange
        val cardDetails = CardDetails("1234567890123456", "12", "2025", "123")
        val expectedTokenizedCard = TokenizedCard("tok_123", "3456", "12", "2025")
        coEvery { mockPayrailsClient.tokenizeCard(cardDetails) } returns ApiResult.Success(expectedTokenizedCard)

        // Act
        viewModel.tokenizeCard(cardDetails)

        // Advance dispatcher
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val expectedMessage = "Card Tokenization Success: tok_123 (last 4: 3456)"
        assertEquals(MainUiState(isLoading = false, resultMessage = expectedMessage), viewModel.uiState.value)
        coVerify(exactly = 1) { mockPayrailsClient.tokenizeCard(cardDetails) }
    }

    @Test
    fun `tokenizeCard should update uiState with error message on API failure`() = runTest {
        // Arrange
        val cardDetails = CardDetails("1234567890123456", "12", "2025", "123")
        val errorMessage = "Invalid card details"
        coEvery { mockPayrailsClient.tokenizeCard(cardDetails) } returns ApiResult.Error(Exception(errorMessage))

        // Act
        viewModel.tokenizeCard(cardDetails)

        // Advance dispatcher
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val expectedMessage = "Error tokenizing card: $errorMessage"
        assertEquals(MainUiState(isLoading = false, resultMessage = expectedMessage), viewModel.uiState.value)
        coVerify(exactly = 1) { mockPayrailsClient.tokenizeCard(cardDetails) }
    }

    @Test
    fun `getPayrailsClientSafely should handle uninitialized PayrailsLib`() = runTest {
        // Arrange: Make PayrailsLib.getClient() throw IllegalStateException
        coEvery { PayrailsLib.getClient() } throws IllegalStateException("PayrailsSDK must be initialized")

        // Act
        viewModel.getPaymentDetails("any_id") // Trigger a call that uses getPayrailsClientSafely()

        // Advance dispatcher
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert: ViewModel should update with an error message
        val expectedMessage = "Error: PayrailsSDK must be initialized"
        assertEquals(MainUiState(isLoading = false, resultMessage = expectedMessage), viewModel.uiState.value)
        // Verify that no client method was called
        coVerify(exactly = 0) { mockPayrailsClient.getPayment(any()) }
    }
}
