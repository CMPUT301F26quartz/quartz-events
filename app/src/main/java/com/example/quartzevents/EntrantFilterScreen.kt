package com.example.quartzevents

// US 01.01.02: edits a draft of campsite filters and returns it only when Apply is pressed.

import android.text.format.DateFormat
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quartzevents.ui.theme.QuartzEventsTheme

private val darkGreen = Color(0xFF234F40)
private val backgroundCream = Color(0xFFF8F6ED)
private val pagePadding = 20.dp
private val cornerRadius = 8.dp

/** Search criteria selected by the entrant. Stay dates are currently used for display. */
data class CampsiteFilters(
    val location: String = "",
    val checkIn: String = "",
    val checkOut: String = "",
    val guests: Int? = null
) {
    val isEmpty: Boolean
        get() = location.isBlank() && checkIn.isBlank() && checkOut.isBlank() && guests == null
}

internal fun validStayDates(checkIn: String, checkOut: String, today: String): Boolean {
    if (checkIn.isBlank() && checkOut.isBlank()) return true
    val start = Date(checkIn)
    val end = Date(checkOut)
    return start.isValidDate() && end.isValidDate() &&
        !start.isBefore(Date(today)) && start.isBefore(end)
}

@Composable
fun EntrantFilterScreen(
    initialFilters: CampsiteFilters,
    onBack: () -> Unit,
    onApply: (CampsiteFilters) -> Unit
) {
    var location by remember { mutableStateOf(initialFilters.location) }
    var guestInput by remember { mutableStateOf(initialFilters.guests?.toString().orEmpty()) }
    var checkIn by remember { mutableStateOf(initialFilters.checkIn) }
    var checkOut by remember { mutableStateOf(initialFilters.checkOut) }

    val guestCount = guestInput.trim().toIntOrNull()
    val invalidGuests = guestInput.isNotBlank() && (guestCount == null || guestCount <= 0)
    val today = DateFormat.format("yyyy-MM-dd", System.currentTimeMillis()).toString()
    val invalidDates = !validStayDates(checkIn.trim(), checkOut.trim(), today)

    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundCream)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
            .imePadding()
    ) {
        FilterHeader(onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = pagePadding)
        ) {
            TextButton(
                onClick = {
                    location = ""
                    guestInput = ""
                    checkIn = ""
                    checkOut = ""
                },
                modifier = Modifier.align(Alignment.End)
            ) { Text("Reset", color = darkGreen) }

            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                placeholder = { Text("Edmonton, Alberta") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
                    .semantics { contentDescription = "Location" },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = darkGreen)
            )

            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = checkIn,
                onValueChange = { checkIn = it },
                label = { Text("Check-in") },
                placeholder = { Text("YYYY-MM-DD") },
                singleLine = true,
                isError = invalidDates,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = darkGreen)
            )
            OutlinedTextField(
                value = checkOut,
                onValueChange = { checkOut = it },
                label = { Text("Check-out") },
                placeholder = { Text("YYYY-MM-DD") },
                singleLine = true,
                isError = invalidDates,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = darkGreen)
            )
            if (invalidDates) {
                Text("Enter valid YYYY-MM-DD dates. Check-in must be today or later, and check-out must be after check-in.",
                    color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = guestInput,
                onValueChange = { guestInput = it },
                label = { Text("Number of Guests") },
                placeholder = { Text("Any number") },
                singleLine = true,
                isError = invalidGuests,
                supportingText = if (invalidGuests) {
                    { Text("Enter a whole number greater than 0.") }
                } else null,
                modifier = Modifier.fillMaxWidth()
                    .semantics { contentDescription = "Number of Guests" },
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = darkGreen),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Button(
            onClick = {
                onApply(CampsiteFilters(location.trim(), checkIn.trim(), checkOut.trim(), guestCount))
            },
            enabled = !invalidGuests && !invalidDates,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = pagePadding, vertical = 16.dp)
                .heightIn(min = 48.dp),
            shape = RoundedCornerShape(cornerRadius),
            colors = ButtonDefaults.buttonColors(containerColor = darkGreen, contentColor = Color.White)
        ) {
            Text("Apply Filters", fontWeight = FontWeight.Bold)
        }
    }

}

@Composable
private fun FilterHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(darkGreen)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Text(
            text = "Filter Campsites",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun EntrantFilterScreenPreview() {
    QuartzEventsTheme {
        EntrantFilterScreen(
            initialFilters = CampsiteFilters(location = "Edmonton, Alberta", guests = 2),
            onBack = {},
            onApply = {}
        )
    }
}
