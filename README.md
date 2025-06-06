## 🚧 Under Construction
# Payrails Android SDK (POC/WIP) 

## Streamlining Payments on Android with Payrails

This project serves as a Proof-of-Concept (POC) and Work-In-Progress (WIP) Android SDK for integrating with the Payrails platform. Payrails is designed to simplify complex international payment landscapes, offering advanced payment orchestration, smart routing, and comprehensive revenue management capabilities.

The goal of this Android SDK is to provide a native interface for Android applications, enabling seamless, secure, and customizable payment experiences while significantly reducing the the merchant's PCI DSS compliance burden.

### Core Value Proposition

This SDK aims to empower Android developers to build robust payment flows that inherently benefit from Payrails' sophisticated backend intelligence. This includes:

* **Payment Acceptance:** Facilitating various payment methods.
* **Smart Routing & Tokenization:** Leveraging Payrails' features for optimized transaction paths.
* **Customizable Checkout:** Offering flexibility in the user payment journey.
* **Reduced PCI DSS Scope:** By implementing client-side tokenization.

---

## 📋 Payrails API Coverage & Roadmap

This SDK implements key Payrails platform features for Android. The table below shows which features are already available and which are recommended for future development, based on the [Payrails API Reference](https://docs.payrails.com/reference/).

### ✅ Implemented Features

- **Authentication:** Secure token management and refresh
- **Payment Initiation:** Start a payment with amount, method, and merchant reference
- **Payment Details:** Fetch payment status and metadata
- **Payment Capture:** Capture funds from authorized payments
- **Client-side Card Tokenization:** Collect and tokenize card details natively (PCI DSS reduction)
- **Composable UI Components:**  
  - Card input (Jetpack Compose)  
  - Web-based checkout UI (for 3DS, redirects, etc.)
- **Environment Switching:** Staging and production support
- **Robust API Error Handling:** Unified result handling
- **Sample integration app and documentation**

### Planned Features

| Feature Area                         | Status        | Notes / Reference                                        |
|-------------------------------------- |-------------- |---------------------------------------------------------|
| **Refunds (full/partial)**            | ❌ Missing    | Add endpoint and flow for refunding payments             |
| **Voids/Cancellation**                | ❌ Missing    | Cancel/void payments before settlement                   |
| **Saved Payment Methods (Vaulting)**  | ❌ Missing    | Support storing/reusing tokens, one-click flows          |
| **Alternative Payment Methods (APMs)**| ❌ Missing    | Support wallets, bank debits, PayPal, etc.               |
| **3D Secure & SCA**                   | ⚠️ Basic      | Handle full 3DS flows, challenges, result parsing        |
| **Payment Status Polling**            | ❌ Missing    | Polling, async status, or webhook handoff                |
| **Advanced Error Mapping**            | ⚠️ Basic      | Expose all API error codes, user-friendly messages       |
| **Custom UI Hooks/Events**            | ❌ Missing    | Allow integrators to extend or customize UI              |
| **API Endpoint Coverage**             | ⚠️ Partial    | Compare SDK interface to all Payrails REST endpoints     |
| **Testing/Mock Mode**                 | ❌ Missing    | Enable easier integration tests & demo use               |

#### Legend
- ✅ Available
- ⚠️ Partial/basic
- ❌ To be added

### 📈 How to Contribute

- See the [Payrails API Reference](https://docs.payrails.com/reference/) for full endpoint details.
- PRs for missing endpoints, new payment methods, or UX enhancements are welcome!
- Please check the [Issues](../../issues) for feature requests and progress.
  
---

## Architectural Highlights

The SDK is structured for modularity and maintainability, separating concerns between core API interactions and UI components.

* **Modular Design:**
    * `payrails-lib`: The core SDK module containing API interfaces, authentication logic, token management, and reusable UI components.
    * `app`: A sample application demonstrating how to integrate and use the `payrails-lib`.
* **Robust Authentication:**
    * **`TokenManager`**: Manages the acquisition, caching, and proactive refreshing of access tokens, including a `60-second buffer` for expiration.
    * **`AuthTokenAuthenticator`**: An OkHttp `Authenticator` that automatically intercepts `401 Unauthorized` responses and triggers token refresh, ensuring seamless re-authentication without blocking the UI thread.
* **Client-Side Card Tokenization:**
    * **`CardTokenizer`**: A dedicated component for securely collecting and tokenizing card details before they leave the device, significantly reducing PCI DSS scope for the merchant.
    * **`CardDetails`**: A data class representing raw card input, intended only for immediate tokenization.
* **API Client (`PayrailsClient`):**
    * Provides high-level methods for interacting with the Payrails API, including fetching payment details, initiating payments, and capturing payments.
    * Wraps API responses in a `sealed class ApiResult<out T>` for clear success and error handling.
* **Composable UI Components (Jetpack Compose):**
    * **`CardInputView`**: A reusable Composable for secure card detail input.
    * **`CheckoutWebView`**: A versatile Composable for handling web-based checkout flows, capable of detecting success/failure based on redirect URLs.
* **Kotlin Coroutines:** Utilized throughout for efficient asynchronous operations and structured concurrency.
* **Environment Configuration:** Supports `STAGING` and `PRODUCTION` environments for easy switching of API endpoints.
* **Kotlin 2.0 & Compose Compatibility:** Configured to work with Kotlin 2.0.21 and a compatible Compose compiler extension (`1.6.0-beta01`), addressing common build issues related to these versions.

---
## Getting Started

To get started with the Payrails Android SDK, follow these steps:

### Prerequisites

* Android Studio Jellyfish or newer.
* JDK 11 or higher.
* An Android device or emulator running API level 26 (Android 8.0) or higher.

### Installation

The project is structured as a multi-module Gradle project.

1.  **Clone the repository:**
    ```bash
    git clone [repository_url]
    cd payrails-android-sdk
    ```

2.  **Gradle Configuration:**
    Ensure your `settings.gradle.kts` includes the `payrails-lib` module:
    ```kotlin
    // settings.gradle.kts
    include ':app', ':payrails-lib'
    ```

3.  **`libs.versions.toml` (Version Catalog):**
    Your `gradle/libs.versions.toml` defines all dependencies:

    ```toml
    # gradle/libs.versions.toml
    [versions]
    agp = "8.10.1"
    kotlin = "2.0.21"
    coreKtx = "1.16.0"
    junit = "4.13.2"
    junitVersion = "1.2.1"
    espressoCore = "3.6.1"
    lifecycleRuntimeKtx = "2.9.0"
    activityCompose = "1.10.1"
    composeBom = "2025.05.01" # IMPORTANT: Use a stable version if you face issues, e.g., "2024.04.00"
    appcompat = "1.7.0"
    material = "1.12.0" # For View-based material design if needed, keep only if actually used
    lifecycleViewmodelCompose = "2.9.0"
    kotlinxCoroutines = "1.7.3"
    materialVersion = "1.8.2" # This is for androidx.compose.material:material (Material 1)
    navigationCompose = "2.9.0"
    okhttp = "4.11.0"
    retrofit = "2.9.0"

    [libraries]
    androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
    androidx-lifecycle-viewmodel-compose = { module = "androidx.lifecycle:lifecycle-viewmodel-compose", version.ref = "lifecycleViewmodelCompose" }
    # androidx-material = { module = "androidx.compose.material:material", version.ref = "materialVersion" } # Use material3 instead
    junit = { group = "junit", name = "junit", version.ref = "junit" }
    androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
    androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
    androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
    androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
    androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
    androidx-ui = { group = "androidx.compose.ui", name = "ui" }
    androidx-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
    androidx-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
    androidx-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
    androidx-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
    androidx-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
    androidx-material3 = { group = "androidx.compose.material3", name = "material3" } # Recommended for Compose apps
    androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
    material = { group = "com.google.android.material", name = "material", version.ref = "material" } # For View-based material design if needed
    androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
    retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
    retrofit-converter-gson = { group = "com.squareup.retrofit2", name = "converter-gson", version.ref = "retrofit" }
    okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
    kotlinx-coroutines-android = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "kotlinxCoroutines" }

    [plugins]
    android-application = { id = "com.android.application", version.ref = "agp" }
    kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
    kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" } # Required for Kotlin 2.0+ Compose
    android-library = { id = "com.android.library", version.ref = "agp" }
    ```

4.  **`module:app` (`app/build.gradle.kts`):**
    ```gradle
    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.android)
        alias(libs.plugins.kotlin.compose) # Ensure this is present
    }

    android {
        // ... (namespace, compileSdk, defaultConfig, buildTypes, compileOptions, kotlinOptions)
        buildFeatures {
            compose = true
        }
    }

    dependencies {
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(platform(libs.androidx.compose.bom)) // Use the BOM for all Compose dependencies
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3) // Ensure you're using Material 3
        implementation(libs.androidx.navigation.compose)
        implementation(libs.androidx.lifecycle.viewmodel.compose)
        implementation(project(":payrails-lib")) // Dependency on your library module

        // ... (testImplementation, androidTestImplementation, debugImplementation)
    }
    ```

5.  **`module:payrails-lib` (`payrails-lib/build.gradle.kts`):**
    ```gradle
    plugins {
        alias(libs.plugins.android.library)
        alias(libs.plugins.kotlin.android)
        alias(libs.plugins.kotlin.compose) # <--- CRITICAL: Ensure this is present for Compose modules with Kotlin 2.0+
    }

    android {
        // ... (namespace, compileSdk, defaultConfig, buildTypes, compileOptions, kotlinOptions)
        composeOptions {
            kotlinCompilerExtensionVersion = "1.6.0-beta01" // Compatible with Kotlin 2.0.21
        }
        buildFeatures {
            compose = true // Ensure compose is enabled for the library
        }
    }

    dependencies {
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material) // Keep if you use View-based Material widgets, otherwise remove

        implementation(platform(libs.androidx.compose.bom)) // Align Compose versions with the app module
        implementation(libs.androidx.material3) // Use Material 3 Composables for your library's UI
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview) // If you need preview support in the lib
        implementation(libs.androidx.activity.compose) // If your library needs ComponentActivity in Composables

        implementation(libs.retrofit)
        implementation(libs.retrofit.converter.gson)
        implementation(libs.okhttp)
        implementation(libs.kotlinx.coroutines.android)
        api(libs.androidx.navigation.compose) // 'api' allows consumers to access NavController directly if needed

        // ... (testImplementation, androidTestImplementation)
    }
    ```

6.  **Sync Gradle Project:** After making these changes, sync your Gradle project to apply the new configurations.

---

## Usage Examples

### 1. Initialization

Initialize the SDK in your `Application` class or `MainActivity`'s `onCreate`:

```kotlin
// In MainActivity.kt or your Application class
import com.mirzakhanidehkordi.payrails_lib.config.Environment
import com.mirzakhanidehkordi.payrails_lib.config.PayrailsLib

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        PayrailsLib.initialize("your_client_id_here", Environment.STAGING)
        // For production, use Environment.PRODUCTION
        // PayrailsLib.initialize("your_production_client_id", Environment.PRODUCTION)

        setContent {
            // Your Compose UI
        }
    }
}
```
### 2. Access the initialized client to perform API operations:

```kotlin
import com.mirzakhanidehkordi.payrails_lib.client.PayrailsClient
import com.mirzakhanidehkordi.payrails_lib.config.PayrailsLib
import com.mirzakhanidehkordi.payrails_lib.api.ApiResult

// Inside a ViewModel or a suspendable context
private suspend fun getPayrailsClientSafely(): PayrailsClient? {
    return try {
        PayrailsLib.getClient()
    } catch (e: IllegalStateException) {
        // Handle initialization error, e.g., show a message to the user
        // _uiState.value = _uiState.value.copy(resultMessage = e.message ?: "SDK not initialized")
        null
    }
}

// Example usage in a ViewModel
fun getPaymentDetails(paymentId: String) {
    viewModelScope.launch {
        val client = getPayrailsClientSafely() ?: return@launch
        when (val result = client.getPayment(paymentId)) {
            is ApiResult.Success -> { /* handle success */ }
            is ApiResult.Error -> { /* handle error */ }
        }
    }
}
```
### 3. Client-Side Card Tokenization
Use `CardInputView` to collect details and then `PayrailsClient` to tokenize:

```kotlin
// In your Composable (e.g., a payment screen)
import com.mirzakhanidehkordi.payrails_lib.ui.CardInputView
import com.mirzakhanidehkordi.payrails_lib.auth.CardDetails
import com.mirzakhanidehkordi.payrails_lib.client.PayrailsClient
import com.mirzakhanidehkordi.payrails_lib.config.PayrailsLib

@Composable
fun PaymentScreen(viewModel: PaymentViewModel = viewModel()) {
    CardInputView { cardDetails ->
        viewModel.tokenizeCard(cardDetails)
    }
}

// In your PaymentViewModel
class PaymentViewModel : ViewModel() {
    fun tokenizeCard(cardDetails: CardDetails) {
        viewModelScope.launch {
            val client = PayrailsLib.getClient() // Ensure initialized
            when (val result = client.tokenizeCard(cardDetails)) {
                is ApiResult.Success -> {
                    val tokenizedCard = result.data
                    // Send tokenizedCard.tokenId to your backend
                    // Handle success, e.g., navigate to confirmation
                }
                is ApiResult.Error -> {
                    // Handle error, show message to user
                }
            }
        }
    }
}
```
### 4. Handling Web Checkout Flows
Use `CheckoutWebView` for 3D Secure or other redirected payment methods:

```kotlin
// In your CheckoutScreen.kt
import com.mirzakhanidehkordi.payrails_lib.ui.CheckoutWebView

@Composable
fun CheckoutScreen(navController: NavController) {
    CheckoutWebView(
        url = "https://your-payrails-checkout-url.com/some_path", // Provided by Payrails for the payment session
        successRedirectUrl = "https://your-app.com/payrails/success", // Your app's deep link or specific URL for success
        failureRedirectUrl = "https://your-app.com/payrails/failure", // Your app's deep link or specific URL for failure
        onComplete = {
            // Payment completed successfully, navigate back or to a success screen
            navController.popBackStack()
        },
        onError = { errorMessage ->
            // Payment failed or was cancelled, show error message
            // Log.e("CheckoutScreen", "Checkout WebView Error: $errorMessage")
            navController.popBackStack()
        }
    )
}
```
