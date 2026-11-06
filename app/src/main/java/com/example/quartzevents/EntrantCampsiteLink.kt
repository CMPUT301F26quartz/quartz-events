package com.example.quartzevents

//US 01.12.01 As an entrant, I want to view campsite details by scanning the promotional QR code.

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

/**
 * Extracts a campsite document ID from a quartz://campsite/{id} link.
 *
 * @param link The campsite link supplied by a future QR scanner.
 * @return The document ID, or null when the link format is invalid.
 */
internal fun campsiteIdFromLink(link: String): String? {
    val uri = Uri.parse(link.trim())
    if (uri.scheme != "quartz" || uri.host != "campsite" || uri.pathSegments.size != 1) {
        return null
    }
    val id = uri.pathSegments[0]
    return id.takeIf { it.isNotBlank() && !it.contains('/') }
}

/**
 * Displays campsite details from a promotional link for US 01.12.01.
 *
 * This is a standalone entry point. QR scanning and external link routing are not connected yet.
 *
 * @param link A link in the form quartz://campsite/{id}.
 * @param onBack Returns to the caller when leaving the details page.
 */
@Composable
fun EntrantCampsiteLink(link: String, onBack: () -> Unit) {
    val campsiteId = remember(link) { campsiteIdFromLink(link) }
    if (campsiteId == null) {
        BackHandler(onBack = onBack)
        Text("Invalid campsite link.")
        return
    }

    val repository = remember { CampsiteInfoRepository() }
    DisposableEffect(repository) {
        onDispose { repository.stopListening() }
    }
    CampsiteDetailsScreen(campsiteId, onBack, repository)
}
