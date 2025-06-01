package com.mirzakhanidehkordi.payrails_lib.api

import retrofit2.http.*

/**
 * Retrofit interface for interacting with the Payrails API.
 * Defines the API endpoints and their respective request/response types.
 */
interface PayrailsApi {
    /**
     * Fetches an authentication token for a given client ID.
     * @param clientId The unique identifier for the client.
     * @return [TokenResponse] containing the access token.
     */
    @POST("auth/token/{clientId}")
    suspend fun getToken(@Path("clientId") clientId: String): TokenResponse

    /**
    Potentially add an endpoint for client-side tokenization (if Payrails supports it)
     This would typically involve sending raw card details *directly* to Payrails' tokenization service
     and receiving a token, thus avoiding your backend handling raw card data.
    */
   // @POST("tokenization/cards") // Example endpoint, verify with Payrails docs
   // suspend fun tokenizeCard(@Body cardDetails: CardTokenizationRequest): CardTokenResponse

    /**
     * Initiates a new payment.
     * This is a common step before capturing or authorizing.
     * The response might include a redirect URL for 3DS or APM.
     * @param request [PaymentInitiationRequest] containing payment details.
     * @return [PaymentInitiationResponse] which might include a checkout URL.
     */
    @POST("payments") // Example endpoint, verify with Payrails docs
    suspend fun initiatePayment(@Body request: PaymentInitiationRequest): PaymentInitiationResponse

    /**
     * Retrieves details of a specific payment by its ID.
     * @param paymentId The unique identifier of the payment.
     * @return [Payment] object containing payment details.
     */
    @GET("payments/{paymentId}")
    suspend fun getPayment(@Path("paymentId") paymentId: String): Payment

    /**
     * Initiates a capture operation for a given payment.
     * @param paymentId The unique identifier of the payment to capture.
     * @param request [CaptureRequest] containing the amount to capture.
     * @return [CaptureResponse] detailing the outcome of the capture.
     */
    @POST("payments/{paymentId}/capture")
    suspend fun capturePayment(
        @Path("paymentId") paymentId: String,
        @Body request: CaptureRequest
    ): CaptureResponse

    /**
     *  Add other payment operations as needed (e.g., refund, void, authorize)
     */
//    @POST("payments/{paymentId}/refund")
//    suspend fun refundPayment(
//        @Path("paymentId") paymentId: String,
//        @Body request: RefundRequest
//    ): RefundResponse

}