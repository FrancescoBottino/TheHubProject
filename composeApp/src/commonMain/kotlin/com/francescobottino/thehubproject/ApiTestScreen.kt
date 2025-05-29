// In composeApp/src/commonMain/kotlin/your_package_name/ui/screens/ApiTestScreen.kt
package com.francescobottino.thehubproject // Adjust package

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.francescobottino.thehubproject.model.AuthRequest
import com.francescobottino.thehubproject.network.ApiService
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
fun ApiTestScreen() {
    val di = localDI()
    val apiService: ApiService by di.instance()
    val coroutineScope = rememberCoroutineScope()

    // --- State Variables ---
    var regUsername by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var loginUsername by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }

    var apiResponse by remember { mutableStateOf("API Response will appear here.") }
    var isLoggedIn by remember { mutableStateOf(false) } // Simple auth state indicator

    var webSocketSession by remember { mutableStateOf<DefaultClientWebSocketSession?>(null) }
    var wsInputMessage by remember { mutableStateOf("Test message") }
    val wsReceivedMessages = remember { mutableStateListOf<String>() }
    var wsConnectionStatus by remember { mutableStateOf("WebSocket: Disconnected") }

    var isLoading by remember { mutableStateOf(false) }

    suspend fun updateAuthStatus() {
        isLoggedIn = apiService.isLoggedIn()
    }

    // Initial check for token
    LaunchedEffect(Unit) {
        updateAuthStatus()
    }

    // Effect to listen to incoming WebSocket messages when session is active
    LaunchedEffect(webSocketSession) {
        val session = webSocketSession
        if (session != null && session.isActive) {
            wsConnectionStatus = "WebSocket: Connected"
            try {
                for (frame in session.incoming) {
                    if (frame is Frame.Text) {
                        val receivedText = frame.readText()
                        wsReceivedMessages.add("Received: $receivedText")
                    } else if (frame is Frame.Close) {
                        wsConnectionStatus = "WebSocket: Closed by remote"
                        break
                    }
                }
            } catch (e: Exception) {
                if (e is ClosedReceiveChannelException) {
                    wsConnectionStatus = "WebSocket: Channel closed"
                } else {
                    wsConnectionStatus = "WebSocket: Error - ${e.message}"
                    wsReceivedMessages.add("Error: ${e.message}")
                }
            } finally {
                if (webSocketSession == session) { // Only update if this is still the active session
                    webSocketSession = null // Clear session if loop exits
                    if (!wsConnectionStatus.contains("Error") && !wsConnectionStatus.contains("Closed by remote")) {
                        wsConnectionStatus = "WebSocket: Disconnected (listener ended)"
                    }
                }
            }
        } else {
            wsConnectionStatus = "WebSocket: Disconnected"
        }
    }

    // Cleanup WebSocket session on dispose
    DisposableEffect(Unit) {
        onDispose {
            coroutineScope.launch { webSocketSession?.close() }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Make content scrollable
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("KMP API Test Screen", style = MaterialTheme.typography.headlineSmall)
        Text("Auth Status: ${if (isLoggedIn) "Logged In" else "Logged Out"}")
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }

        HorizontalDivider()

        // --- Registration ---
        Text("Registration", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = regUsername, onValueChange = { regUsername = it }, label = { Text("Reg Username") })
        OutlinedTextField(
            value = regPassword,
            onValueChange = { regPassword = it },
            label = { Text("Reg Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Button(onClick = {
            isLoading = true
            coroutineScope.launch {
                val result = apiService.register(AuthRequest(regUsername, regPassword))
                result.onSuccess {
                    apiResponse = "Register Success: ${it.message}\nToken: ${it.token.take(15)}..."
                    updateAuthStatus()
                }.onFailure {
                    apiResponse = "Register Error: ${it.message}"
                    updateAuthStatus()
                }
                isLoading = false
            }
        }, enabled = !isLoading) { Text("Register") }

        HorizontalDivider()

        // --- Login ---
        Text("Login", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(value = loginUsername, onValueChange = { loginUsername = it }, label = { Text("Login Username") })
        OutlinedTextField(
            value = loginPassword,
            onValueChange = { loginPassword = it },
            label = { Text("Login Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Button(onClick = {
            isLoading = true
            coroutineScope.launch {
                val result = apiService.login(AuthRequest(loginUsername, loginPassword))
                result.onSuccess {
                    apiResponse = "Login Success: ${it.message}\nToken: ${it.token.take(15)}..."
                    updateAuthStatus()
                }.onFailure {
                    apiResponse = "Login Error: ${it.message}"
                    updateAuthStatus()
                }
                isLoading = false
            }
        }, enabled = !isLoading) { Text("Login") }

        HorizontalDivider()

        // --- Protected API ---
        Text("Protected API", style = MaterialTheme.typography.titleMedium)
        Button(onClick = {
            isLoading = true
            coroutineScope.launch {
                val result = apiService.getMyProfile()
                result.onSuccess {
                    apiResponse = "Profile: User ${it.username} (ID: ${it.id})"
                }.onFailure {
                    apiResponse = "Profile Error: ${it.message}"
                }
                isLoading = false
            }
        }, enabled = !isLoading && isLoggedIn) { Text("Get My Profile (/me)") }

        HorizontalDivider()

        // --- Logout ---
        Button(onClick = {
            isLoading = true
            coroutineScope.launch {
                apiService.logout()
                apiResponse = "Logged out."
                updateAuthStatus()
                isLoading = false
            }
        }, enabled = !isLoading && isLoggedIn) { Text("Logout") }

        HorizontalDivider()
        
        // --- WebSocket Echo Test (New Interaction Model) ---
        Text("WebSocket Echo Interactive", style = MaterialTheme.typography.titleMedium)
        Text(wsConnectionStatus)

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (webSocketSession == null) {
                        wsConnectionStatus = "WebSocket: Connecting..."
                        isLoading = true
                        coroutineScope.launch {
                            val session = apiService.establishEchoWebSocketSession()
                            if (session != null) {
                                webSocketSession = session
                                // Listening is handled by LaunchedEffect(webSocketSession)
                            } else {
                                wsConnectionStatus = "WebSocket: Connection Failed"
                            }
                            isLoading = false
                        }
                    }
                },
                enabled = webSocketSession == null && !isLoading
            ) { Text("Connect WS") }

            Button(
                onClick = {
                    coroutineScope.launch {
                        webSocketSession?.close()
                        webSocketSession = null // Listener effect will update status
                    }
                },
                enabled = webSocketSession != null && !isLoading
            ) { Text("Disconnect WS") }
        }

        OutlinedTextField(
            value = wsInputMessage,
            onValueChange = { wsInputMessage = it },
            label = { Text("Message to send via WS") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val session = webSocketSession
                if (session != null && session.isActive && wsInputMessage.isNotBlank()) {
                    coroutineScope.launch {
                        try {
                            session.send(Frame.Text(wsInputMessage))
                            wsReceivedMessages.add("Sent: $wsInputMessage")
                            wsInputMessage = "" // Clear input after send
                        } catch (e: Exception) {
                            wsReceivedMessages.add("Error sending: ${e.message}")
                            // Potentially close session or mark as error
                        }
                    }
                } else {
                    wsReceivedMessages.add("Not connected or message empty.")
                }
            },
            enabled = webSocketSession != null && webSocketSession?.isActive == true && !isLoading
        ) { Text("Send WS Message") }

        Spacer(Modifier.height(8.dp))
        Text("Received WS Messages (last 5):")
        Column {
            wsReceivedMessages.takeLast(5).forEach { message ->
                Text(message, style = MaterialTheme.typography.bodySmall)
            }
        }

        HorizontalDivider()

        Text("API Response:", style = MaterialTheme.typography.titleSmall)
        Text(apiResponse, style = MaterialTheme.typography.bodyMedium)
    }
}