package com.mirzakhanidehkordi.payrails_lib.ui

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

/**
 * A Composable function that displays a WebView for handling checkout processes.
 * It's designed to load a given URL and trigger a callback when a specific
 * "success" pattern is detected in the page URL, indicating completion.
 *
 * @param url The URL to load in the WebView.
 * @param onComplete A lambda function to be invoked when the checkout process is considered complete.
 */
@Composable
fun CheckoutWebView(url: String, onComplete: () -> Unit) {
    AndroidView(factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true // Enable JavaScript for interactive web content
            webViewClient = object : WebViewClient() {
                /**
                 * Called when a page finishes loading.
                 * Checks if the loaded URL contains "success" to trigger the [onComplete] callback.
                 */
                override fun onPageFinished(view: WebView?, pageUrl: String?) {
                    // This is a simple success check. For robust production apps,
                    // consider more secure and specific callbacks from your webview
                    // (e.g., JavaScript interface, deep links, or specific redirect URLs).
                    if (pageUrl?.contains("success") == true) {
                        onComplete()
                    }
                }
            }
            loadUrl(url) // Load the specified URL into the WebView
        }
    })
}