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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quartzevents.ui.theme.QuartzEventsTheme

private val darkGreen = Color(0xFF244D3D)
private val backgroundCream = Color(0xFFF7F5ED)
private val cardGray = Color(0xFFD9D9D9)
private val textDark = Color(0xFF303834)
private val textGray = Color(0xFF686868)
private val pagePadding = 18.dp
private val cornerRadius = 8.dp

/**
 * Stores the entrant's selected search criteria.
 *
 * @property location A case-insensitive part of the campsite location, or blank for any location.
 * @property checkIn Check-in date in YYYY-MM-DD format, or blank to skip date filtering.
 * @property checkOut Check-out date in YYYY-MM-DD format, or blank to skip date filtering.
 * @property guests Minimum required campsite capacity, or null for any capacity.
 */
data class CampsiteFilters(
    val location: String = "",
    val checkIn: String = "",
    val checkOut: String = "",
    val guests: Int? = null
) {
    /** Whether no location, dates, or guest count have been selected. */
    val isEmpty: Boolean
        get() = location.isBlank() && checkIn.isBlank() && checkOut.isBlank() && guests == null
}

/**
 * Checks that stay dates are both blank or form a valid future or current stay.
 *
 * @param checkIn Check-in date in YYYY-MM-DD format, or blank.
 * @param checkOut Check-out date in YYYY-MM-DD format, or blank.
 * @param today Today's date in YYYY-MM-DD format.
 * @return Whether check-in is today or later and check-out is after check-in, or both dates are blank.
 */
internal fun validStayDates(checkIn: String, checkOut: String, today: String): Boolean {
    if (checkIn.isBlank() && checkOut.isBlank()) return true
    val start = Date(checkIn)
    val end = Date(checkOut)
    return start.isValidDate() && end.isValidDate() &&
        !start.isBefore(Date(today)) && start.isBefore(end)
}

/**
 * Edits a local set of search filters and returns them when Apply Filters is pressed.
 *
 * @param initialFilters Previously selected filters used to populate the fields.
 * @param onBack Leaves the page without applying the local changes.
 * @param onApply Receives the location, stay dates, and guest count after validation.
 */
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

    var showDates by remember { mutableStateOf(false) }

    val guestCount = guestInput.trim().toIntOrNull()
    val invalidGuests = guestInput.isNotBlank() && (guestCount == null || guestCount <= 0)
    val today = DateFormat.format("yyyy-MM-dd", System.currentTimeMillis()).toString()
    val invalidDates = !validStayDates(checkIn.trim(), checkOut.trim(), today)

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = cardGray, unfocusedContainerColor = cardGray, errorContainerColor = cardGray,
        focusedBorderColor = darkGreen, unfocusedBorderColor = Color.Transparent,
        focusedTextColor = textDark, unfocusedTextColor = textDark, errorTextColor = textDark,
        focusedPlaceholderColor = textGray, unfocusedPlaceholderColor = textGray,
        focusedLabelColor = darkGreen, unfocusedLabelColor = textGray,
        cursorColor = darkGreen
    )

    if (showDates) {
        AlertDialog(
            onDismissRequest = { showDates = false },
            containerColor = backgroundCream,
            title = { Text("Stay dates", color = darkGreen) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = checkIn,
                        onValueChange = { checkIn = it },
                        label = { Text("Check-in") },
                        placeholder = { Text("YYYY-MM-DD") },
                        singleLine = true,
                        isError = invalidDates,
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors,
                        shape = RoundedCornerShape(cornerRadius)
                    )
                    OutlinedTextField(
                        value = checkOut,
                        onValueChange = { checkOut = it },
                        label = { Text("Check-out") },
                        placeholder = { Text("YYYY-MM-DD") },
                        singleLine = true,
                        isError = invalidDates,
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors,
                        shape = RoundedCornerShape(cornerRadius)
                    )
                    if (invalidDates) {
                        Text("Enter valid YYYY-MM-DD dates. Check-in must be today or later, and check-out must be after check-in.",
                            color = MaterialTheme.colorScheme.error)
                    }

                }
            },
            confirmButton = {
                TextButton(onClick = { showDates = false }, enabled = !invalidDates) {
                    Text("Done", color = darkGreen)
                }
            }
        )
    }

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
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Filter Your Search", color = darkGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                TextButton(
                    onClick = {
                        location = ""
                        guestInput = ""
                        checkIn = ""
                        checkOut = ""
                    }
                ) { Text("Reset", color = darkGreen) }
            }

            Spacer(Modifier.height(20.dp))
            Text("Location", color = darkGreen, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text("Edmonton, Alberta") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
                    .semantics { contentDescription = "Location" }.testTag("locationInput"),
                colors = fieldColors,
                shape = RoundedCornerShape(cornerRadius)
            )

            Spacer(Modifier.height(24.dp))
            Text("Date", color = darkGreen, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Surface(onClick = { showDates = true }, color = cardGray,
                shape = RoundedCornerShape(cornerRadius), modifier = Modifier.fillMaxWidth()) {
                Text(if (checkIn.isBlank() && checkOut.isBlank()) "Select dates" else "$checkIn – $checkOut",
                    color = textDark, modifier = Modifier.padding(16.dp))
            }
            if (invalidDates) {
                Text("Choose valid check-in and check-out dates.", color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))
            Text("Number of Guests", color = darkGreen, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = guestInput,
                onValueChange = { guestInput = it },
                placeholder = { Text("Any number") },
                singleLine = true,
                isError = invalidGuests,
                supportingText = if (invalidGuests) {
                    { Text("Enter a whole number greater than 0.") }
                } else null,
                modifier = Modifier.fillMaxWidth()
                    .semantics { contentDescription = "Number of Guests" }.testTag("guestInput"),
                colors = fieldColors,
                shape = RoundedCornerShape(cornerRadius),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.height(48.dp))
            Text("Choose filters, then apply to view matches.", color = textGray, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
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

/** Displays the filter page title and back button. */
@Composable
private fun FilterHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(darkGreen)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            .padding(start = 8.dp, end = 16.dp, top = 40.dp),
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

/** Previews the filter page with a sample location and guest count. */
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
