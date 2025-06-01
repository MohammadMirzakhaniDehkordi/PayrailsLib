package com.mirzakhanidehkordi.payrails_lib.api

/**
 * Data class representing the response for an authentication token.
 * @property accessToken The actual token to be used for authorization.
 * @property tokenType The type of token (e.g., "Bearer").
 * @property expiresIn The duration in seconds until the token expires.
 */
data class TokenResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Int
)

/**
 * Data class representing a payment entity.
 * @property id The unique identifier of the payment.
 * @property status The current status of the payment (e.g., "PENDING", "COMPLETED").
 * @property history A list of operations performed on this payment.
 */
data class Payment(
    val id: String,
    val status: String,
    val history: List<PaymentOperation>
)

/**
 * Data class representing a single operation within a payment's history.
 * @property id The unique identifier for this operation.
 * @property type The type of operation (e.g., "CAPTURE", "REFUND").
 * @property result The outcome of the operation (e.g., "SUCCESS", "FAILED").
 * @property responseCode The response code from the payment gateway.
 * @property log A list of logs related to this operation.
 */
data class PaymentOperation(
    val id: String,
    val type: String,
    val result: String,
    val responseCode: String,
    val log: List<PaymentOperationLog>
)

/**
 * Data class representing a log entry for a payment operation.
 * @property id The unique identifier for this log entry.
 * @property request The raw request sent for the operation.
 * @property response The raw response received for the operation.
 */
data class PaymentOperationLog(
    val id: String,
    val request: String,
    val response: String
)

/**
 * Data class representing a request to capture a payment.
 * @property amount The amount to be captured.
 */
data class CaptureRequest(val amount: Amount)

/**
 * Data class representing an amount with its value and currency.
 * @property value The monetary value as a string.
 * @property currency The currency code (e.g., "EUR", "USD").
 */
data class Amount(val value: String, val currency: String)

/**
 * Data class representing the response of a capture operation.
 * @property success Indicates if the capture was successful.
 * @property action The action performed (e.g., "CAPTURE").
 * @property amount The amount that was captured.
 * @property execution Details about the execution of the capture.
 */
data class CaptureResponse(
    val success: Boolean,
    val action: String,
    val amount: Amount,
    val execution: Execution
)

/**
 * Data class representing the execution details of an operation.
 * @property id The unique identifier of the execution.
 * @property merchantReference A reference provided by the merchant for this transaction.
 */
data class Execution(
    val id: String,
    val merchantReference: String
)

/**
 * Data class representing the sensitive card details collected from the UI.
 * This should ideally *not* be sent directly to your backend or Payrails API for PCI compliance.
 * Instead, use a Payrails client-side tokenization mechanism.
 */
data class CardDetails(
    val cardNumber: String,
    val expiryMonth: String,
    val expiryYear: String,
    val cvv: String
)

data class CardTokenizationRequest(
    val cardNumber: String,
    val expiryMonth: String,
    val expiryYear: String,
    val cvv: String
)

data class CardTokenResponse(
    val cardToken: String,
    val cardBrand: String,
    // ... other card details if needed
)

data class PaymentInitiationRequest(
    val amount: Amount,
    val currency: String,
    val merchantReference: String,
    val paymentMethod: PaymentMethodDetails, // e.g., type "card", "paypal", "token"
    val returnUrl: String, // For redirects after 3DS or APM
    val cancelUrl: String
)

data class PaymentMethodDetails(
    val type: String, // "card", "token", "paypal" etc.
    val cardToken: String? = null // If using a pre-tokenized card
    // ... other payment method specific fields
)

data class PaymentInitiationResponse(
    val paymentId: String,
    val status: String,
    val redirectUrl: String? = null, // If 3DS or APM requires redirection
    // ... other response fields
)
