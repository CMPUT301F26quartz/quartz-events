package com.example.quartzevents.EntrantFunctions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quartzevents.Classes.Lottery
import com.example.quartzevents.Repositories.ReservationRepository

@Composable
fun MakeReservationScreen(
    onBack: () -> Unit,
    onReservationCreated: () -> Unit
) {
    val repository = remember { ReservationRepository() }

    var lotteries by remember {
        mutableStateOf<List<Pair<String, Lottery>>>(emptyList())
    }

    var selectedLotteryId by remember { mutableStateOf("") }
    var selectedCampsiteId by remember { mutableStateOf("") }

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var guests by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(true) }
    var submitting by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var showConfirmation by remember { mutableStateOf(false) }

    // Load lotteries won by the current entrant.
    LaunchedEffect(Unit) {
        repository.getWonLotteries { result, error ->
            lotteries = result ?: emptyList()
            message = error ?: ""
            loading = false
        }
    }

    val selectedLottery = lotteries.find {
        it.first == selectedLotteryId
    }?.second

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        TextButton(onClick = onBack) {
            Text("Back")
        }

        Text(
            text = "Make Reservation",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator()
        } else if (lotteries.isEmpty()) {
            if (message.isBlank()) {
                Text("You have not won any lotteries yet.")
            }
        } else {

            Text("Select a lottery")

            lotteries.forEach { (id, lottery) ->

                Row {
                    RadioButton(
                        selected = selectedLotteryId == id,
                        onClick = {
                            selectedLotteryId = id
                            selectedCampsiteId = ""
                        }
                    )

                    Text(
                        text = lottery.name,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }

            selectedLottery?.let { lottery ->

                Spacer(modifier = Modifier.height(16.dp))

                Text("Select a campsite")

                lottery.campsites.forEach { campsite ->

                    Row {
                        RadioButton(
                            selected = selectedCampsiteId == campsite.id,
                            onClick = {
                                selectedCampsiteId = campsite.id.orEmpty()
                            }
                        )

                        Text(
                            text = campsite.name,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("Check-in (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                label = { Text("Check-out (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = guests,
                onValueChange = { guests = it },
                label = { Text("Number of guests") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                enabled = !submitting &&
                        selectedLotteryId.isNotBlank() &&
                        selectedCampsiteId.isNotBlank(),

                modifier = Modifier.fillMaxWidth(),

                onClick = {

                    val guestCount = guests.toIntOrNull()

                    if (guestCount == null || guestCount < 1) {
                        message = "Please enter a valid guest count."
                    } else {

                        submitting = true
                        message = ""

                        repository.makeReservation(
                            lotteryId = selectedLotteryId,
                            campsiteId = selectedCampsiteId,
                            guests = guestCount,
                            startDate = startDate,
                            endDate = endDate
                        ) { success, result ->

                            submitting = false

                            if (success) {
                                showConfirmation = true
                            } else {
                                message = result
                            }
                        }
                    }
                }
            ) {
                Text(
                    if (submitting) "Booking..."
                    else "Confirm Reservation"
                )
            }
        }

        if (message.isNotBlank()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    // Display confirmation after a successful reservation.
    if (showConfirmation) {

        AlertDialog(
            onDismissRequest = {},

            title = {
                Text("Reservation Confirmed")
            },

            text = {
                Text(
                    "Your campsite reservation has been " +
                            "successfully created."
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmation = false
                        onReservationCreated()
                    }
                ) {
                    Text("View Reservations")
                }
            }
        )
    }
}