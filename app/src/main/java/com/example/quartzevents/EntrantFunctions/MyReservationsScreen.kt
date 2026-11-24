package com.example.quartzevents.EntrantFunctions

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quartzevents.Classes.Reservation
import com.example.quartzevents.Repositories.ReservationRepository
import java.time.LocalDate

@Composable
fun MyReservationsScreen(onBack: () -> Unit) {
    val repository = remember { ReservationRepository() }

    var reservations by remember {
        mutableStateOf<List<Reservation>>(emptyList())
    }
    var loading by remember { mutableStateOf(true) }
    var message by remember { mutableStateOf("") }
    var showPast by remember { mutableStateOf(false) }
    var cancelling by remember { mutableStateOf(false) }

    var selectedReservation by remember {
        mutableStateOf<Reservation?>(null)
    }

    fun loadReservations() {
        loading = true

        repository.getMyReservations { result, error ->
            reservations = result ?: emptyList()
            message = error ?: ""
            loading = false
        }
    }

    LaunchedEffect(Unit) {
        loadReservations()
    }

    val today = LocalDate.now().toString()

    val visibleReservations = reservations.filter { reservation ->
        val isPast = reservation.endDate <= today ||
                reservation.status == "cancelled"

        if (showPast) isPast else !isPast
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(20.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("Back")
        }
        Text(
            text = "My Reservations",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = { showPast = false }) {
                Text("Active")
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { showPast = true }) {
                Text("Past / Cancelled")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotBlank()) {
            Text(message)
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (loading) {
            CircularProgressIndicator()
        } else if (visibleReservations.isEmpty()) {
            Text("No reservations found.")
        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    visibleReservations,
                    key = { it.id }
                ) { reservation ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Campsite ID: ${reservation.campsiteId}")
                            Text("Check-in: ${reservation.startDate}")
                            Text("Check-out: ${reservation.endDate}")
                            Text("Guests: ${reservation.guests}")
                            Text("Status: ${reservation.status}")

                            if (
                                !showPast &&
                                reservation.status == "confirmed" &&
                                reservation.startDate >= today
                            ) {
                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )
                                Button(
                                    onClick = {
                                        selectedReservation = reservation
                                    }
                                ) {
                                    Text("Cancel Reservation")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedReservation?.let { reservation ->

        AlertDialog(
            onDismissRequest = {
                if (!cancelling) {
                    selectedReservation = null
                }
            },

            title = {
                Text("Cancel Reservation?")
            },

            text = {
                Text(
                    "Are you sure you want to cancel this reservation?"
                )
            },

            confirmButton = {

                TextButton(
                    enabled = !cancelling,
                    onClick = {

                        cancelling = true

                        repository.cancelReservation(
                            reservation.id
                        ) { success, result ->

                            cancelling = false
                            selectedReservation = null

                            if (success) {
                                loadReservations()
                            } else {
                                message = result
                            }
                        }
                    }
                ) {
                    Text("Yes, Cancel")
                }
            },

            dismissButton = {

                TextButton(
                    enabled = !cancelling,
                    onClick = {
                        selectedReservation = null
                    }
                ) {
                    Text("Keep Reservation")
                }
            }
        )
    }
}