package com.example.quartzevents.OwnerFunctions

import android.R.attr.description
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
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

import com.example.quartzevents.Classes.Campsite
import com.google.firebase.firestore.FirebaseFirestore
import com.example.quartzevents.uploadToImgBB


private val QuartzGreen = Color(0xFF254D3C)
private val QuartzWhite = Color(0xFFFFFFFF)
private val QuartzCream = Color(0xFFF7F5ED)


/**
 * Displays a new campsite form.
 * Takes user input.
 * Saves to Firestore.
 *
 * @param onBack Cancels and goes back.
 * @param onSaveSuccess Triggers after saving.
 */

@Composable
fun RegisterCampsite(
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    campsiteToEdit: Campsite? = null

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

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    LaunchedEffect(campsiteToEdit) {
        campsiteToEdit?.let { existingCampsite ->
            campsiteName = existingCampsite.name
            location = existingCampsite.location
            description = existingCampsite.description
            maxOccupancy = existingCampsite.maxOccupancy
            tags = existingCampsite.tags
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) selectedImageUri = uri
    }
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
                OutlinedTextField( // referenced https://kotlinlang.org/api/compose-multiplatform/material3/androidx.compose.material3/-outlined-text-field.html and https://kotlinlang.org/api/compose-multiplatform/material3/androidx.compose.material3/-outlined-text-field-defaults/colors.html
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
                    text = "Description/Amenities",
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
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Text(
                        text = "Tags by comma",
                        fontWeight = FontWeight.Bold,
                        color = QuartzGreen,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                    )
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        placeholder = { Text("Forest, Lake, Hot tub etc", color = Color.Gray) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFD9D9D9),
                            unfocusedContainerColor = Color(0xFFD9D9D9),
                            focusedBorderColor = QuartzGreen,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }

                Button(
                    onClick = {
                        pickImageLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text("Select Optional Image")
                }

                selectedImageUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected Image Preview",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }



            }
        }



        Button(
            onClick = {
                errorMessage = null

                if (campsiteName.isBlank() || location.isBlank()) {
                    errorMessage = "Please fill in the required fields."
                } else {
                    isLoading = true

                    scope.launch {
                        var uploadedUrl = campsiteToEdit?.imageUrl
                        if (selectedImageUri != null) {
                            uploadedUrl = uploadToImgBB(context, selectedImageUri!!)
                        }

                        val newCampsite = Campsite(
                            id = campsiteToEdit?.id ?: "",
                            ownerId = currentUserId,
                            name = campsiteName.trim(),
                            location = location.trim(),
                            description = description.trim(),
                            maxOccupancy = maxOccupancy.trim(),
                            tags = tags.trim(),
                            imageUrl = uploadedUrl
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