package com.example.composeapp.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeapp.viewmodel.AuthViewModel
import com.example.composeapp.viewmodel.AuthState
import com.example.composeapp.ui.components.AppLogo
import com.example.composeapp.ui.components.ErrorMessage
import com.dilip.composeapp.R


@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPref = remember { context.getSharedPreferences("auth_prefs", android.content.Context.MODE_PRIVATE) }
    
    var email by remember { mutableStateOf(sharedPref.getString("remembered_email", "") ?: "") }
    var password by remember { mutableStateOf(sharedPref.getString("remembered_password", "") ?: "") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(sharedPref.getBoolean("remember_me", false)) }
    val authState by authViewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppLogo(size = 120.dp)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; authViewModel.resetState() },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; authViewModel.resetState() },
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                Text(
                    stringResource(R.string.remember_me), 
                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                )
            }
            Text(
                stringResource(R.string.forgot_password),
                modifier = Modifier.clickable {
                    if (email.isBlank()) {
                        authViewModel.setError("Please enter your email first")
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        authViewModel.setError("Please enter a valid email address")
                    } else {
                        authViewModel.forgotPassword(email)
                    }
                },
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    authViewModel.setError("Email and password cannot be empty")
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    authViewModel.setError("Please enter a valid email address")
                } else {
                    // Save or Clear "Remember Me" preference
                    sharedPref.edit().apply {
                        putBoolean("remember_me", rememberMe)
                        if (rememberMe) {
                            putString("remembered_email", email.trim())
                            putString("remembered_password", password)
                        } else {
                            remove("remembered_email")
                            remove("remembered_password")
                        }
                        apply()
                    }
                    authViewModel.login(email.trim(), password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        ) {
            Text(stringResource(R.string.login))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.no_account),
            modifier = Modifier.clickable {
                authViewModel.resetState()
                navController.navigate("registration")
            },
            color = MaterialTheme.colorScheme.primary
        )

        when (val state = authState) {
            is AuthState.Error -> ErrorMessage(state.message)
            is AuthState.Success -> {
                LaunchedEffect(Unit) { navController.navigate("home") }
            }
            else -> {}
        }
    }
}

@Composable
fun RegistrationScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppLogo(size = 120.dp)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.full_name)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = mobile,
            onValueChange = { mobile = it },
            label = { Text(stringResource(R.string.mobile_number)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (name.isBlank() || mobile.isBlank() || email.isBlank() || password.isBlank()) {
                    authViewModel.setError("All fields are required")
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    authViewModel.setError("Please enter a valid email address")
                } else if (password.length < 6) {
                    authViewModel.setError("Password must be at least 6 characters")
                } else {
                    authViewModel.register(email, password, name, mobile)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = authState !is AuthState.Loading
        ) {
            Text(stringResource(R.string.register))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            stringResource(R.string.has_account),
            modifier = Modifier.clickable {
                authViewModel.resetState()
                navController.navigate("login")
            },
            color = MaterialTheme.colorScheme.primary
        )

        when (val state = authState) {
            is AuthState.Error -> ErrorMessage(state.message)
            is AuthState.Success -> Text(state.message, color = MaterialTheme.colorScheme.primary)
            else -> {}
        }
    }
}
