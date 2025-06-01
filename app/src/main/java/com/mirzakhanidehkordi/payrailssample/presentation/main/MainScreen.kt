package com.mirzakhanidehkordi.payrailssample.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mirzakhanidehkordi.payrailssample.presentation.navigation.AppScreens

/**
 * Composable for the main screen of the application.
 * It observes state from [MainViewModel] and dispatches UI events.
 *
 * @param navController The NavController for navigating between screens.
 * @param viewModel The [MainViewModel] instance, typically provided by Hilt or default ViewModel factory.
 */
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    // Observe state from ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { viewModel.getPaymentDetails("fe67fc45-ce47-4fc8-a283-22d03ae68b77") },
            enabled = !uiState.isLoading
        ) {
            Text("Get Payment Details")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.capturePayment("fe67fc45-ce47-4fc8-a283-22d03ae68b77", 12.50, "EUR") },
            enabled = !uiState.isLoading
        ) {
            Text("Capture Payment")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(AppScreens.Checkout.route) },
            enabled = !uiState.isLoading
        ) {
            Text("Start Checkout")
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = uiState.resultMessage)
        }
    }
}