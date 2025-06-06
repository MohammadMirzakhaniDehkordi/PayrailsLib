package com.mirzakhanidehkordi.payrailssample.presentation.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Use LaunchedEffect to show Snackbar when resultMessage changes
    LaunchedEffect(uiState.resultMessage) {
        if (uiState.resultMessage.isNotBlank() && !uiState.isLoading) {
            snackbarHostState.showSnackbar(uiState.resultMessage)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(AppScreens.Tokenization.route) },
                enabled = !uiState.isLoading
            ) {
                Text("Tokenize Card")
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
                Text("Loading...")
            }

        }
    }
}