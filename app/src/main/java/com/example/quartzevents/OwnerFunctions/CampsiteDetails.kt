package com.example.quartzevents.OwnerFunctions

import androidx.compose.runtime.Composable
import com.example.quartzevents.EntrantFunctions.CampsiteDetailsScreen

/** Keeps the owner dashboard entry point connected to the shared details page. */
@Composable
fun CampsiteDetails(campsiteId: String, onBack: () -> Unit) {
    CampsiteDetailsScreen(campsiteId, onBack = onBack, showOwnerActions = true)
}
