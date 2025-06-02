package com.mirzakhanidehkordi.payrailssample.presentation.checkout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mirzakhanidehkordi.payrails_lib.ui.CheckoutWebView

/**
 * Composable for the checkout screen, displaying a WebView.
 *
 * @param navController The NavController for handling navigation back to the previous screen.
 */
@Composable
fun CheckoutScreen(navController: NavController) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // IMPORTANT: Replace with a real Payrails checkout URL and REAL redirect URLs from Lib documentation
        // These URLs are crucial for the WebView to correctly identify success and failure
        CheckoutWebView(
            url = "https://example.com/your-payrails-checkout-page", // Replace with your actual Payrails checkout URL
            successRedirectUrl = "https://yourapp.com/payrails/success", // Replace with your app's success redirect URL
            failureRedirectUrl = "https://yourapp.com/payrails/failure", // Replace with your app's failure redirect URL
            onComplete = {
                errorMessage = null
                navController.popBackStack()
            },
            onError = { error ->
                errorMessage = error
            }
        )

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Error: $it")
        }
    }
}