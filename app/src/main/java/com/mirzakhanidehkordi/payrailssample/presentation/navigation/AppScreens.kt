package com.mirzakhanidehkordi.payrailssample.presentation.navigation

/**
 * Sealed class to define application navigation routes.
 * This provides a type-safe way to manage navigation paths.
 */
sealed class AppScreens(val route: String) {
    object Main : AppScreens("main_screen")
    object Checkout : AppScreens("checkout_screen")

    // Can add arguments to routes like this:
    // object Detail : AppScreens("detail_screen/{itemId}") {
    //     fun createRoute(itemId: String) = "detail_screen/$itemId"
    // }
}