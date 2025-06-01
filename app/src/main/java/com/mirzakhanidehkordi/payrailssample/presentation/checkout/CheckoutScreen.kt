package com.mirzakhanidehkordi.payrailssample.presentation.checkout


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mirzakhanidehkordi.payrails_lib.ui.CheckoutWebView // Assuming this is your updated CheckoutWebView

/**
 * Composable for the checkout screen, displaying a WebView.
 *
 * @param navController The NavController for handling navigation back to the previous screen.
 */
@Composable
fun CheckoutScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // IMPORTANT: Replace with a real Payrails checkout URL and proper redirect URLs from Lib documentation
        CheckoutWebView(
            url = "https://your-payrails-checkout-url.com/some_path", // Replace with the actual URL provided by Payrails for the checkout flow
            successRedirectUrl = "https://your-app.com/payrails/success", // Replace with your app's designated success redirect URL (or a custom scheme)
            failureRedirectUrl = "https://your-app.com/payrails/failure", // Replace with your app's designated failure redirect URL (or a custom scheme)
            onComplete = {
                // Handle successful checkout, e.g., navigate to a success screen
                navController.popBackStack()
                // You might also want to show a success message or fetch updated payment status
            },
            onError = { errorMessage ->
                // Handle checkout errors, e.g., show a Toast or navigate back with an error message
                // Log.e("CheckoutScreen", "Checkout WebView Error: $errorMessage")
                navController.popBackStack() // Or navigate to an error screen
                // Show a user-friendly error message
            }
        )
    }
}