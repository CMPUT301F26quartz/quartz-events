package com.example.quartzevents

// US 01.01.03 / 01.01.04: displays matching campsites and their summary information.

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val darkGreen = Color(0xFF234F40)
private val backgroundCream = Color(0xFFF8F6ED)
private val cardGray = Color(0xFFD9D9D9)
private val textDark = Color(0xFF303834)
private val textGray = Color(0xFF686868)

/** Campsite details keyed by Firestore document ID. Missing ratings and prices stay null. */
data class CampsiteInfo(
    val id: String,
    val name: String,
    val location: String,
    val maxOccupancy: Int? = null,
    val rating: Double? = null,
    val description: String = "",
    val imageUrl: String = "",
    val tags: String = "",
    val price: Double? = null,
    val rules: String = "",
    val cancellationPolicy: String = ""
)

internal fun readCampsite(id: String, fields: Map<String, Any>): CampsiteInfo {
    fun text(key: String) = (fields[key] as? String).orEmpty().trim()
    fun number(key: String) = fields[key]?.toString()?.toDoubleOrNull()

    val tags = when (val value = fields["tags"]) {
        is String -> value.trim()
        is List<*> -> value.filterIsInstance<String>().joinToString(" · ")
        else -> ""
    }
    return CampsiteInfo(
        id = id,
        name = text("name"),
        location = text("location"),
        maxOccupancy = number("maxOccupancy")?.toInt(),
        rating = number("rating"),
        description = text("description"),
        imageUrl = text("imageUrl"),
        tags = tags,
        price = number("price"),
        rules = text("rules"),
        cancellationPolicy = text("cancellationPolicy")
    )
}


internal fun filterCampsites(
    campsites: List<CampsiteInfo>,
    searchText: String,
    filters: CampsiteFilters
): List<CampsiteInfo> {
    val name = searchText.trim()
    val location = filters.location.trim()
    return campsites.filter { campsite ->
        val nameMatches = campsite.name.contains(name, ignoreCase = true)
        val locationMatches = campsite.location.contains(location, ignoreCase = true)
        val guestsMatch = filters.guests == null ||
            (campsite.maxOccupancy != null && campsite.maxOccupancy >= filters.guests)
        nameMatches && locationMatches && guestsMatch
    }.sortedBy { it.name.lowercase() }
}

internal fun describeFilters(filters: CampsiteFilters): String {
    val parts = mutableListOf<String>()
    if (filters.location.isNotBlank()) parts.add(filters.location)
    if (filters.checkIn.isNotBlank() && filters.checkOut.isNotBlank()) {
        parts.add("${filters.checkIn} – ${filters.checkOut}")
    }
    if (filters.guests != null) parts.add("Guests: ${filters.guests}")
    return parts.joinToString(" · ").ifEmpty { "No filters selected" }
}

@Composable
fun EntrantSearchResultsScreen(
    searchText: String,
    filters: CampsiteFilters,
    campsites: List<CampsiteInfo>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onCampsiteClick: (String) -> Unit
) {
    val matches = filterCampsites(campsites, searchText, filters)
    val hasDates = filters.checkIn.isNotBlank()
    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundCream)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
    ) {
        ResultsHeader(onBack)
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Matching Campsites", color = darkGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            item {
                Text(if (searchText.isBlank()) "All campsites" else "Search: $searchText", color = textDark)
            }
            item {
                Text(describeFilters(filters), color = textDark)
            }
            if (hasDates) {
                item {
                    Text(
                        "Dates are not verified. Results match your name, location and guest filters only.",
                        color = textGray, fontSize = 14.sp
                    )
                }
            }
            when {
                isLoading -> item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = darkGreen)
                    }
                }
                errorMessage != null -> item {
                    Text(errorMessage, color = textDark)
                    TextButton(onClick = onRetry) { Text("Retry", color = darkGreen) }
                }
                else -> {
                    item {
                        Text("Results: ${matches.size}", color = darkGreen, fontWeight = FontWeight.Bold)
                    }
                    if (matches.isEmpty()) {
                        item { Text("No campsites match. Try another name, location or guest count.", color = textGray) }
                    }
                    items(matches, key = { it.id }) { campsite ->
                        ResultCard(campsite, onClick = { onCampsiteClick(campsite.id) })
                    }
                }
            }
        }
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp).heightIn(min = 48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = darkGreen, contentColor = Color.White)
        ) {
            Text("Back to Search", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ResultsHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(darkGreen)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text("Search Results", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun ResultCard(campsite: CampsiteInfo, onClick: () -> Unit) {
    Surface(onClick = onClick, color = cardGray, shape = RoundedCornerShape(8.dp)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(campsite.name, color = textDark,
                    fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(campsite.location, color = textGray, fontSize = 13.sp)
                if (campsite.maxOccupancy != null) {
                    Text("Up to ${campsite.maxOccupancy} guests", color = textGray, fontSize = 13.sp)
                }
                if (campsite.rating != null) {
                    Text("★ ${campsite.rating} / 5",
                        color = darkGreen, fontSize = 13.sp)
                }
            }
            Text("›", color = darkGreen, fontSize = 26.sp, modifier = Modifier.padding(start = 12.dp))
        }
    }
}
