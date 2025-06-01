package com.mirzakhanidehkordi.payrails_lib.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.mirzakhanidehkordi.payrails_lib.auth.CardDetails

@Composable
fun CardInputView(onCardDetailsEntered: (CardDetails) -> Unit) {
    val cardNumberState = remember { mutableStateOf("") }
    val expiryMonthState = remember { mutableStateOf("") }
    val expiryYearState = remember { mutableStateOf("") }
    val cvvState = remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = cardNumberState.value,
            onValueChange = { cardNumberState.value = it },
            label = { Text("Card Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            OutlinedTextField(
                value = expiryMonthState.value,
                onValueChange = { expiryMonthState.value = it },
                label = { Text("MM") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = expiryYearState.value,
                onValueChange = { expiryYearState.value = it },
                label = { Text("YY") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = cvvState.value,
            onValueChange = { cvvState.value = it },
            label = { Text("CVV") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val cardDetails = CardDetails(
                    cardNumberState.value,
                    expiryMonthState.value,
                    expiryYearState.value,
                    cvvState.value
                )
                onCardDetailsEntered(cardDetails)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tokenize Card")
        }
    }
}