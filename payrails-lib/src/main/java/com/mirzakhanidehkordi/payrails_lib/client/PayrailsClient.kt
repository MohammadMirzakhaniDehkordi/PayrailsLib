package com.mirzakhanidehkordi.payrails_lib.client

import com.mirzakhanidehkordi.payrails_lib.api.ApiResult
import com.mirzakhanidehkordi.payrails_lib.api.Amount
import com.mirzakhanidehkordi.payrails_lib.api.CaptureRequest
import com.mirzakhanidehkordi.payrails_lib.api.CaptureResponse
import com.mirzakhanidehkordi.payrails_lib.api.Payment
import com.mirzakhanidehkordi.payrails_lib.api.PayrailsApi
import com.mirzakhanidehkordi.payrails_lib.auth.CardDetails
import com.mirzakhanidehkordi.payrails_lib.auth.TokenManager
import com.mirzakhanidehkordi.payrails_lib.auth.TokenizedCard
import com.mirzakhanidehkordi.payrails_lib.auth.tokenizeCard

/**
 * Main client for interacting with the Payrails API.
 * Contain the core business logic for interacting with the Payrails API.
 * Provides methods for fetching payment details and capturing payments,
 * wrapping results in an [ApiResult] for error handling.
 *
 * @param api The [PayrailsApi] interface instance for making network calls.
 * @param tokenManager The [TokenManager] responsible for providing authentication tokens.
 */
class PayrailsClient(
    private val api: PayrailsApi, // Public to allow advanced customization; use with caution
    private val tokenManager: TokenManager
) {
    /**
     * Fetches details of a specific payment.
     * @param paymentId The unique identifier of the payment.
     * @return An [ApiResult.Success] containing the [Payment] object on success,
     * or an [ApiResult.Error] with an [Exception] on failure.
     */
    suspend fun getPayment(paymentId: String): ApiResult<Payment> {
        return try {
            val payment = api.getPayment(paymentId)
            ApiResult.Success(payment)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    /**
     * Initiates a capture operation for a given payment.
     * @param paymentId The unique identifier of the payment to capture.
     * @param amount The amount to be captured.
     * @param currency The currency of the amount.
     * @return An [ApiResult.Success] containing the [CaptureResponse] on success,
     * or an [ApiResult.Error] with an [Exception] on failure.
     */
    suspend fun capturePayment(
        paymentId: String,
        amount: Double,
        currency: String
    ): ApiResult<CaptureResponse> {
        return try {
            val request = CaptureRequest(Amount(amount.toString(), currency))
            val response = api.capturePayment(paymentId, request)
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    /**
     * Tokenizes the provided card details using the internal API.
     * @param cardDetails The card details to tokenize.
     * @return An [ApiResult] with the tokenized card or an error.
     */
    suspend fun tokenizeCard(cardDetails: CardDetails): ApiResult<TokenizedCard> {
        return try {
            val response = api.tokenizeCard(cardDetails) // Delegate to internal api
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}