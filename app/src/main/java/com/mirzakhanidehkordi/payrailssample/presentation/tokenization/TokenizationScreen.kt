package com.mirzakhanidehkordi.payrailssample.presentation.tokenization

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.mirzakhanidehkordi.payrails_lib.auth.CardDetails
import com.mirzakhanidehkordi.payrails_lib.ui.CardInputView
import com.mirzakhanidehkordi.payrailssample.presentation.main.MainViewModel

/**
 * Composable for the card tokenization screen.
 * It uses the [CardInputView] from the PayrailsLib to collect card details
 * and triggers the tokenization process via [MainViewModel].
 *
 * @param navController The NavController for handling navigation.
 * @param viewModel The [MainViewModel] instance to interact with PayrailsLib.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenizationScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Enter Card Details for Tokenization")
            Spacer(modifier = Modifier.height(16.dp))

            CardInputView(
                onCardDetailsEntered = { cardDetails: CardDetails ->
                    viewModel.tokenizeCard(cardDetails)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
                Text("Tokenizing card...")
            }
        }
    }
}