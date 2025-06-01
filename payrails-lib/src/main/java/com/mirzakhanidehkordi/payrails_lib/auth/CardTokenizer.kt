package com.mirzakhanidehkordi.payrails_lib.auth

import com.mirzakhanidehkordi.payrails_lib.api.ApiResult
import com.mirzakhanidehkordi.payrails_lib.api.PayrailsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/***
 * This module provides a secure way to collect and tokenize card details, reducing PCI DSS scope.
 * CardTokenizer assumes a tokenization endpoint (e.g., POST /tokens) exists in Payrails’ API.
 * need to confirm this with their support or documentation.
 * */
data class CardDetails(
    val cardNumber: String,
    val expiryMonth: String,
    val expiryYear: String,
    val cvv: String
)

data class TokenizedCard(
    val tokenId: String,
    val lastFour: String,
    val expiryMonth: String,
    val expiryYear: String
)

class CardTokenizer(private val api: PayrailsApi) {
    suspend fun tokenizeCard(cardDetails: CardDetails): ApiResult<TokenizedCard> = withContext(Dispatchers.IO) {
        try {
            // Validate card details (simplified for demo)
            if (!isValidCard(cardDetails)) throw IllegalArgumentException("Invalid card details")

            // Simulate tokenization API call (replace with actual Payrails endpoint)
            val response = api.tokenizeCard(cardDetails) // Hypothetical endpoint
            ApiResult.Success(response)
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    private fun isValidCard(cardDetails: CardDetails): Boolean {
        // Basic format validation
        return cardDetails.cardNumber.length in 13..19 &&
                cardDetails.expiryMonth.toIntOrNull() in 1..12 &&
                cardDetails.expiryYear.length == 4 &&
                cardDetails.cvv.length in 3..4
    }
}

// Hypothetical extension to PayrailsApi
suspend fun PayrailsApi.tokenizeCard(cardDetails: CardDetails): TokenizedCard {
    // This is a placeholder; replace with actual Payrails tokenization endpoint
    return TokenizedCard(
        tokenId = "tok_${System.currentTimeMillis()}",
        lastFour = cardDetails.cardNumber.takeLast(4),
        expiryMonth = cardDetails.expiryMonth,
        expiryYear = cardDetails.expiryYear
    )
}