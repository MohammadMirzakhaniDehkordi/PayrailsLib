package com.mirzakhanidehkordi.payrails_lib.api


sealed class ApiResult<out T> {
    /**
     * Represents a successful API response with the [data].
     */
    data class Success<out T>(val data: T) : ApiResult<T>()

    /**
     * Represents an API error with an [exception] detailing the failure.
     */
    data class Error(val exception: Exception) : ApiResult<Nothing>()
}