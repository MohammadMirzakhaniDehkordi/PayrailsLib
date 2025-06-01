package com.mirzakhanidehkordi.payrailssample.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mirzakhanidehkordi.payrails_lib.client.PayrailsClient
import com.mirzakhanidehkordi.payrails_lib.api.ApiResult
import com.mirzakhanidehkordi.payrails_lib.auth.CardDetails
import com.mirzakhanidehkordi.payrails_lib.auth.CardTokenizer
import com.mirzakhanidehkordi.payrails_lib.config.PayrailsLib
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Data class representing the UI state of the MainScreen.
 * @property isLoading Indicates if an operation is currently in progress.
 * @property resultMessage The message to display to the user, reflecting operation outcome or errors.
 */
data class MainUiState(
    val isLoading: Boolean = false,
    val resultMessage: String = "Results will appear here"
)

/**
 * ViewModel for the MainScreen.
 * It manages the UI state and interacts with the PayrailsLib to perform operations.
 */
class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    // We no longer lazily initialize payrailsClient here.
    // Instead, we will get it inside the coroutine scope for each operation.


    /**
     * Helper function to safely get the PayrailsClient.
     * @return PayrailsClient if initialized, otherwise null.
     */
    private fun getPayrailsClientSafely(): PayrailsClient? {
        return try {
            PayrailsLib.getClient()
        } catch (e: IllegalStateException) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                resultMessage = "Error: PayrailsLib not initialized. Call PayrailsLib.initialize() first."
            )
            null
        } catch (e: Exception) { // Catch any other unexpected errors during client access
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                resultMessage = "Error: An unexpected error occurred while getting PayrailsClient: ${e.message}"
            )
            null
        }
    }


    /**
     * Fetches payment details from the Payrails SDK.
     * Updates the UI state based on the operation's success or failure.
     *
     * @param paymentId The ID of the payment to retrieve.
     */
    fun getPaymentDetails(paymentId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, resultMessage = "Fetching payment details...")

            val client = getPayrailsClientSafely()
            if (client == null) {
                // Error message already set by getPayrailsClientSafely()
                return@launch
            }

            when (val result = client.getPayment(paymentId)) { // Use 'client' here
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        resultMessage = "Payment Status: ${result.data.status}",
                        isLoading = false
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        resultMessage = "Error fetching payment: ${result.exception.message ?: "Unknown error"}",
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Initiates a payment capture operation via the Payrails SDK.
     * Updates the UI state based on the operation's success or failure.
     *
     * @param paymentId The ID of the payment to capture.
     * @param amount The amount to capture.
     * @param currency The currency of the amount.
     */
    fun capturePayment(paymentId: String, amount: Double, currency: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, resultMessage = "Capturing payment...")

            val client = getPayrailsClientSafely()
            if (client == null) {
                // Error message already set by getPayrailsClientSafely()
                return@launch
            }

            when (val result = client.capturePayment(paymentId, amount, currency)) { // Use 'client' here
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        resultMessage = "Capture Success: ${result.data.success}",
                        isLoading = false
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        resultMessage = "Error capturing payment: ${result.exception.message ?: "Unknown error"}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun tokenizeCard(cardDetails: CardDetails) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, resultMessage = "Tokenizing card...")
            val client = getPayrailsClientSafely() ?: return@launch

            when (val result = client.tokenizeCard(cardDetails)) {
                is ApiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        resultMessage = "Tokenized: ${result.data.tokenId}",
                        isLoading = false
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        resultMessage = "Tokenization failed: ${result.exception.message}",
                        isLoading = false
                    )
                }
            }
        }
    }
}