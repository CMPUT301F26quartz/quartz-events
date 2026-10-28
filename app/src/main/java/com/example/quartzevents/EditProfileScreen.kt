package com.example.quartzevents

import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import com.example.quartzevents.model.UserProfile
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// TODO: ASK IF CHANGE PASSWORD IS NEEDED IN EDIT PROFILE!!!

private val darkGreen = Color(0xFF234F40)
private val backgroundCream = Color(0xFFF8F6ED)
private val fieldGray = Color(0xFFD9D9D9)

@Composable
fun EditProfileScreen(
    profile: UserProfile,
    onBack: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    val nameParts = profile.name.trim().split(" ", limit = 2)

    var firstName by remember(profile.uid) {
        mutableStateOf(nameParts.getOrElse(0) { "" })
    }

    var lastName by remember(profile.uid) {
        mutableStateOf(nameParts.getOrElse(1) {""} )
    }

    var username by remember(profile.uid) {
        mutableStateOf(profile.username)
    }

    var email by remember(profile.uid) {
        mutableStateOf(profile.email)
    }

    var phoneNumber by remember(profile.uid) {
        mutableStateOf(profile.phoneNumber)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundCream)
            .safeDrawingPadding()
    ) {
        //green header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(darkGreen)
                .padding(horizontal = 12.dp, vertical = 20.dp),
        ) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    text = "<",
                    color = Color.White,
                    fontSize = 30.sp
                )
            }

            Text(
                text = "Edit Profile",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // personal information section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Personal Information",
                color = darkGreen,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "First Name",
                        color = darkGreen,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = fieldGray,
                            unfocusedContainerColor = fieldGray,
                            focusedBorderColor = darkGreen,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Last Name",
                        color = darkGreen,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = fieldGray,
                            unfocusedContainerColor = fieldGray,
                            focusedBorderColor = darkGreen,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Username",
                        color = darkGreen,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = fieldGray,
                            unfocusedContainerColor = fieldGray,
                            focusedBorderColor = darkGreen,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }

            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Email Address",
                        color = darkGreen,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = fieldGray,
                            unfocusedContainerColor = fieldGray,
                            focusedBorderColor = darkGreen,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Phone Number",
                        color = darkGreen,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = fieldGray,
                            unfocusedContainerColor = fieldGray,
                            focusedBorderColor = darkGreen,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val phoneDigits = phoneNumber.filter { it.isDigit() }

                errorMessage = when {
                    firstName.isBlank() -> "First name is required"
                    lastName.isBlank() -> "Last name is required"
                    username.isBlank() -> "Username is required"
                    email.isBlank() -> "Email address is required"
                    !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                        "Please enter a valid email address"
                    phoneNumber.isBlank() -> "Phone number is required"
                    phoneDigits.length != 10 ->
                        "Please enter a valid 10-digit phone number"
                    else -> ""
                }

                if (errorMessage.isEmpty()) {
                    val updatedProfile = profile.copy(
                        name = "${firstName.trim()} ${lastName.trim()}",
                        username = username.trim(),
                        //email = email.trim()
                        phoneNumber = phoneNumber.trim()
                    )

                    onSave(updatedProfile)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .height(53.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = darkGreen,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Save Changes",
                fontSize = 18.sp
            )
        }
    }
}