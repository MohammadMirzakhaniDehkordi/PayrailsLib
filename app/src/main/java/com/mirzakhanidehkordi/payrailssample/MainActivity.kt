package com.mirzakhanidehkordi.payrailssample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mirzakhanidehkordi.payrails_lib.config.Environment
import com.mirzakhanidehkordi.payrails_lib.config.PayrailsLib
import com.mirzakhanidehkordi.payrailssample.presentation.checkout.CheckoutScreen
import com.mirzakhanidehkordi.payrailssample.presentation.main.MainScreen
import com.mirzakhanidehkordi.payrailssample.presentation.navigation.AppScreens
import com.mirzakhanidehkordi.payrailssample.presentation.tokenization.TokenizationScreen
import com.mirzakhanidehkordi.payrailssample.ui.theme.PayrailsSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        PayrailsLib.initialize("demo_client_id", Environment.STAGING)

        setContent {
            PayrailsSampleTheme  {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = AppScreens.Main.route) {
                    composable(AppScreens.Main.route) { MainScreen(navController = navController) }
                    composable(AppScreens.Checkout.route) { CheckoutScreen(navController = navController) }
                    composable(AppScreens.Tokenization.route) { TokenizationScreen(navController = navController) } // Added TokenizationScreen
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PayrailsSampleTheme {

    }
}