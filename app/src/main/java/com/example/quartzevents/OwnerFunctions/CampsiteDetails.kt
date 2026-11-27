package com.example.quartzevents.OwnerFunctions

import androidx.compose.runtime.Composable
import com.example.quartzevents.EntrantFunctions.CampsiteDetailsScreen

/** Owner entry point to the shared campsite details page. */
@Composable
fun CampsiteDetails(campsiteId: String, onBack: () -> Unit) {
    CampsiteDetailsScreen(campsiteId, onBack = onBack)
}
