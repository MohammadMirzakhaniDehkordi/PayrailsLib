package com.mirzakhanidehkordi.payrailssample.presentation.main.test

import com.mirzakhanidehkordi.payrails_lib.api.*
import com.mirzakhanidehkordi.payrails_lib.auth.CardDetails
import com.mirzakhanidehkordi.payrails_lib.auth.TokenizedCard
import com.mirzakhanidehkordi.payrails_lib.client.PayrailsClient
import com.mirzakhanidehkordi.payrails_lib.config.PayrailsLib
import com.mirzakhanidehkordi.payrailssample.presentation.main.MainViewModel
import io.mockk.*
import io.mockk.unmockkObject
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
        unmockkObject(PayrailsLib)
        unmockkAll()
    }

    @Test
    fun `getPaymentDetails should update uiState with success message on API success`() = runTest {
        val paymentId = "test_payment_id"
        val expectedPayment = Payment(id = paymentId, status = "COMPLETED", history = emptyList())
        coEvery { mockPayrailsClient.getPayment(any()) } returns ApiResult.Success(expectedPayment)

        assertEquals("Results will appear here", viewModel.uiState.value.resultMessage)
        assertFalse(viewModel.uiState.value.isLoading)

        viewModel.getPaymentDetails(paymentId)
        testDispatcher.scheduler.advanceUntilIdle()

        val expectedMessage = "Payment Status: COMPLETED" // <-- FIXED to match ViewModel
        assertEquals(expectedMessage, viewModel.uiState.value.resultMessage)
        assertFalse(viewModel.uiState.value.isLoading)
        coVerify(exactly = 1) { mockPayrailsClient.getPayment(paymentId) }
    }

    @Test
    fun `getPaymentDetails should update uiState with error message on API failure`() = runTest {
        val paymentId = "test_payment_id"
        val errorMessage = "Payment not found"
        coEvery { mockPayrailsClient.getPayment(any()) } returns ApiResult.Error(Exception(errorMessage))

        viewModel.getPaymentDetails(paymentId)
        testDispatcher.scheduler.advanceUntilIdle()

        val expectedMessage = "Error fetching payment: $errorMessage"
        assertEquals(expectedMessage, viewModel.uiState.value.resultMessage)
        assertFalse(viewModel.uiState.value.isLoading)
        coVerify(exactly = 1) { mockPayrailsClient.getPayment(paymentId) }
    }

    @Test
    fun `tokenizeCard should update uiState with success message on API success`() = runTest {
        val cardDetails = CardDetails("1234567890123456", "12", "2025", "123")
        val expectedTokenizedCard = TokenizedCard("tok_123", "3456", "12", "2025")
        coEvery { mockPayrailsClient.tokenizeCard(any()) } returns ApiResult.Success(expectedTokenizedCard)

        viewModel.tokenizeCard(cardDetails)
        testDispatcher.scheduler.advanceUntilIdle()

        val expectedMessage = "Tokenized: tok_123"
        assertEquals(expectedMessage, viewModel.uiState.value.resultMessage)
        assertFalse(viewModel.uiState.value.isLoading)
        coVerify(exactly = 1) { mockPayrailsClient.tokenizeCard(cardDetails) }
    }

    @Test
    fun `tokenizeCard should update uiState with error message on API failure`() = runTest {
        val cardDetails = CardDetails("1234567890123456", "12", "2025", "123")
        val errorMessage = "Invalid card details"
        coEvery { mockPayrailsClient.tokenizeCard(any()) } returns ApiResult.Error(Exception(errorMessage))

        viewModel.tokenizeCard(cardDetails)
        testDispatcher.scheduler.advanceUntilIdle()

        val expectedMessage = "Tokenization failed: $errorMessage"
        assertEquals(expectedMessage, viewModel.uiState.value.resultMessage)
        assertFalse(viewModel.uiState.value.isLoading)
        coVerify(exactly = 1) { mockPayrailsClient.tokenizeCard(cardDetails) }
    }

    @Test
    fun `getPayrailsClientSafely should handle uninitialized PayrailsLib`() = runTest {
        unmockkObject(PayrailsLib)
        mockkObject(PayrailsLib)
        coEvery { PayrailsLib.getClient() } throws IllegalStateException("PayrailsSDK must be initialized")

        viewModel = MainViewModel()
        viewModel.getPaymentDetails("any_id")
        testDispatcher.scheduler.advanceUntilIdle()

        val expectedMessage = "Error: PayrailsLib not initialized. Call PayrailsLib.initialize() first."
        assertEquals(expectedMessage, viewModel.uiState.value.resultMessage)
        assertFalse(viewModel.uiState.value.isLoading)
        coVerify(exactly = 0) { mockPayrailsClient.getPayment(any()) }
    }

}