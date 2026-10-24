package com.example.quartzevents.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.quartzevents.model.UserProfile
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.input.KeyboardType

private val QuartzGreen = Color(0xFF254D3C)
private val QuartzDarkGreen = Color(0xFF244D3D)
private val QuartzSage = Color(0xFF8B958B)
private val QuartzGray = Color(0xFFB3B3B3)
private val QuartzFieldGray = Color(0xFFD9D9D9)
private val QuartzWhite = Color(0xFFFFFFFF)
private val QuartzCream = Color(0xFFF7F5ED)

@Composable
private fun QuartzTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = QuartzDarkGreen,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {Text(placeholder)},
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            keyboardOptions = KeyboardOptions(keyboardType= keyboardType),
            visualTransformation = if (isPassword) {
                PasswordVisualTransformation()
            } else {
                androidx.compose.ui.text.input.VisualTransformation.None
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = QuartzFieldGray,
                unfocusedContainerColor = QuartzFieldGray,
                focusedBorderColor = QuartzGreen,
                unfocusedBorderColor = Color.Transparent
            )
        )
    }
}


@Composable
fun AuthScreen(
    onAuthenticated: (UserProfile) -> Unit
) {
    val repository = remember { AuthRepository() }

    var isSignUp by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var agreedToPolicies by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("")}
    var selectedRole by remember { mutableStateOf("entrant")}
    var isLoading by remember { mutableStateOf(false)}
    var errorMessage by remember { mutableStateOf<String?>(null)}
    val isSignUpValid = firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            username.isNotBlank() &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() &&
            phoneNumber.isNotBlank() &&
            password.length >= 6 &&
            agreedToPolicies


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuartzCream)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(QuartzGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isSignUp) "Let's Get Started" else "Quartz",
                color = QuartzWhite,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = if (isSignUp) "Create Your Account" else "Welcome Back!",
            color = QuartzDarkGreen,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        if (!isSignUp) {
            Text(
                text = "Sign in to continue your adventure.",
                color = QuartzDarkGreen,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (isSignUp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuartzTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = "First Name",
                    placeholder = "First Name",
                    modifier = Modifier.weight(1f)
                )

                QuartzTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = "Last Name",
                    placeholder = "Last Name",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            QuartzTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                placeholder = "Enter a username",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
        QuartzTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "Enter your email",
            keyboardType = KeyboardType.Email,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        QuartzTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            placeholder = "Enter your password",
            isPassword = true,
            keyboardType = KeyboardType.Password,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
        )
        if (isSignUp) {
            Spacer(modifier = Modifier.height(12.dp))

            QuartzTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = "Phone Number",
                placeholder = "XXX-XXX-XXXX",
                keyboardType = KeyboardType.Phone,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            )

            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = agreedToPolicies,
                    onCheckedChange = { agreedToPolicies = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = QuartzGreen
                    )
                )

                Text(
                    text = "I have read and agree to the Policies & Guidelines",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (isSignUp) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Register as:",
                style = MaterialTheme.typography.titleSmall
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedRole == "entrant",
                    onClick = { selectedRole = "entrant"}
                )
                Text("Entrant")
                Spacer(modifier = Modifier.width(20.dp))

                RadioButton(
                    selected = selectedRole == "owner",
                    onClick = { selectedRole = "owner"}
                )
                Text("Owner")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        if(errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = {
                errorMessage = null

                if (email.isBlank() || password.isBlank() ||
                    (isSignUp && (
                            firstName.isBlank() ||
                                    lastName.isBlank() ||
                                    username.isBlank() ||
                                    phoneNumber.isBlank()
                            ))
                ) {
                    errorMessage = "Please fill in all fields."
                } else if (isSignUp && !agreedToPolicies) {
                    errorMessage = "Please agree to the Policies & Guidelines."
                } else {
                    isLoading = true

                    if (isSignUp) {
                        repository.register(
                            name = "${firstName.trim()} ${lastName.trim()}",
                            email = email.trim(),
                            password = password,
                            role = selectedRole,
                            username = username.trim(),
                            phoneNumber = phoneNumber.trim()
                        ) { success, error ->
                            if (success) {
                                val uid = repository.getCurrentUserId()

                                if (uid != null) {
                                    repository.getUserProfile(uid) { profile, profileError ->
                                        isLoading = false
                                        if (profile != null) {
                                            onAuthenticated(profile)
                                        } else {
                                            errorMessage = profileError
                                                ?: "Could not load user profile."
                                        }
                                    }
                                } else {
                                    isLoading = false
                                    errorMessage = "Could not get user ID."
                                }
                            } else {
                                isLoading = false
                                errorMessage = error ?: "Registration failed."
                            }
                        }
                    } else {
                        repository.login(
                            email = email.trim(),
                            password = password
                        ) { success, error ->
                            if (success) {
                                val uid = repository.getCurrentUserId()
                                if (uid != null) {
                                    repository.getUserProfile(uid) { profile, profileError ->
                                        isLoading = false
                                        if (profile != null) {
                                            onAuthenticated(profile)
                                        } else {
                                            errorMessage = profileError
                                                ?: "Could not load user profile."
                                        }
                                    }
                                } else {
                                    isLoading = false
                                    errorMessage = "Could not get user ID."
                                }
                            } else {
                                isLoading = false
                                errorMessage = error ?: "Login failed."
                            }
                        }
                    }
                }
            },
            enabled = !isLoading && (!isSignUp || isSignUpValid),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = QuartzGreen,
                contentColor = QuartzWhite,
                disabledContainerColor = QuartzGray,
                disabledContentColor = QuartzWhite
            )

        ) {
            Text(
                if (isLoading) "Please wait..."
                else if (isSignUp) "Create Account"
                else "Sign In"
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = {
                isSignUp = !isSignUp
                errorMessage = null
            },
            enabled = !isLoading,
            colors = ButtonDefaults.textButtonColors(
                contentColor = QuartzDarkGreen
            )
        ) {
            Text(
                if (isSignUp) "Already have an account? Log In"
                else "Don't have an account? Sign Up",
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
            )
        }
    }
}