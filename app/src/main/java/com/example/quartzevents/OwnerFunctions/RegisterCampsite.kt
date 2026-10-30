package com.example.quartzevents.OwnerFunctions

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.google.firebase.auth.FirebaseAuth

import com.example.quartzevents.OwnerFunctions.Campsite
import com.example.quartzevents.OwnerFunctions.CampsiteRepository


private val QuartzGreen = Color(0xFF254D3C)
private val QuartzWhite = Color(0xFFFFFFFF)
private val QuartzCream = Color(0xFFF7F5ED)

@Composable
fun RegisterCampsite(
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit

) {
    var campsiteName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var maxOccupancy by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    val repository = remember { CampsiteRepository() }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuartzCream)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(QuartzCream)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(QuartzGreen),

            ) {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = QuartzGreen,
                        contentColor = Color.White),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 30.dp, end = 300.dp)
                        .background(color = QuartzGreen)

                ) {
                    Text(
                        text = ">",
                        fontSize = 35.sp
                    )
                }
            }
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Text(
                    text = "Campsite Name",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF254D3C),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                )
                OutlinedTextField(
                    value = campsiteName,
                    onValueChange = { campsiteName = it },
                    placeholder = { Text("Enter", color = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFD9D9D9),
                        unfocusedContainerColor = Color(0xFFD9D9D9),
                        focusedBorderColor = Color(0xFF254D3C),
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                Text(
                    text = "Location",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF254D3C),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("Enter", color = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFD9D9D9),
                        unfocusedContainerColor = Color(0xFFD9D9D9),
                        focusedBorderColor = Color(0xFF254D3C),
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = "Description",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF254D3C),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Enter", color = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFD9D9D9),
                        unfocusedContainerColor = Color(0xFFD9D9D9),
                        focusedBorderColor = Color(0xFF254D3C),
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = "Max Occupancy",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF254D3C),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                )
                OutlinedTextField(
                    value = maxOccupancy,
                    onValueChange = { maxOccupancy = it },
                    placeholder = { Text("Enter", color = Color.Gray) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFD9D9D9),
                        unfocusedContainerColor = Color(0xFFD9D9D9),
                        focusedBorderColor = Color(0xFF254D3C),
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        }



        Button(
            onClick = {
                errorMessage = null

                if (campsiteName.isBlank() || location.isBlank()) {
                    errorMessage = "Please fill in the required fields."
                } else {
                    isLoading = true

                    val newCampsite = Campsite(
                        ownerId = currentUserId,
                        name = campsiteName.trim(),
                        location = location.trim(),
                        description = description.trim(),
                        maxOccupancy = maxOccupancy.trim(),
                        tags = tags.trim()
                    )

                    repository.saveCampsite(newCampsite) { success, error ->
                        isLoading = false
                        if (success) {
                            onSaveSuccess()
                        } else {
                            errorMessage = error ?: "Failed to save campsite."
                        }
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = QuartzGreen,
                contentColor = QuartzWhite
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = QuartzWhite,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Register Campsite")
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 12.dp)
            )
        }




    }
}