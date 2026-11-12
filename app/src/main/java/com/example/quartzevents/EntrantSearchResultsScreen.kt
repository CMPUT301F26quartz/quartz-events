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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val darkGreen = Color(0xFF244D3D)
private val backgroundCream = Color(0xFFF7F5ED)
private val cardGray = Color(0xFFD9D9D9)
private val textDark = Color(0xFF303834)
private val textGray = Color(0xFF686868)

/**
 * Campsite details keyed by Firestore document ID. Missing ratings and prices stay null.
 *
 * @property imageUrl A downloadable HTTP(S) image URL, or blank when no photo is available.
 * @property availableStart Earliest check-in date in YYYY-MM-DD format.
 * @property availableEnd Latest check-out date in YYYY-MM-DD format.
 */
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
    val cancellationPolicy: String = "",
    val availableStart: String = "",
    val availableEnd: String = ""
)

/**
 * Converts Firestore document fields into campsite information for the screens.
 *
 * @param id The Firestore document ID used to open the campsite details.
 * @param fields The document fields, including optional details.
 * @return The campsite information with empty or null values for missing fields.
 */
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
        maxOccupancy = (number("capacity") ?: number("maxOccupancy"))?.toInt(),
        rating = number("rating"),
        description = text("description"),
        imageUrl = text("imageUrl"),
        tags = tags,
        price = number("price"),
        rules = text("rules"),
        cancellationPolicy = text("cancellationPolicy"),
        availableStart = text("availableStart"),
        availableEnd = text("availableEnd")
    )
}


/**
 * Matches campsite names, locations, guest capacity, and available dates, then sorts by name.
 *
 * The entire stay must fit within availableStart and availableEnd, stored as YYYY-MM-DD strings.
 * Campsites without valid availability dates are excluded when stay dates are selected.
 *
 * @param campsites The available campsites to search.
 * @param searchText A case-insensitive part of the campsite name.
 * @param filters The selected location, stay dates, and guest count.
 * @return Campsites that match all the supported filters.
 */
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
        val datesMatch = campsiteHasStayDates(campsite, filters.checkIn, filters.checkOut)
        nameMatches && locationMatches && guestsMatch && datesMatch
    }.sortedBy { it.name.lowercase() }
}

/** Checks that the whole stay fits in the campsite's inclusive availability range. Blank dates skip this filter. */
internal fun campsiteHasStayDates(campsite: CampsiteInfo, checkIn: String, checkOut: String): Boolean {
    if (checkIn.isBlank() && checkOut.isBlank()) return true
    val start = Date(campsite.availableStart)
    val end = Date(campsite.availableEnd)
    val arrival = Date(checkIn)
    val departure = Date(checkOut)
    return start.isValidDate() && end.isValidDate() && arrival.isValidDate() && departure.isValidDate() &&
        arrival.isBefore(departure) && !arrival.isBefore(start) && !departure.isAfter(end)
}

/** Builds a short display summary of the selected location, dates, and guest count. */
internal fun describeFilters(filters: CampsiteFilters): String {
    val parts = mutableListOf<String>()
    if (filters.location.isNotBlank()) parts.add(filters.location)
    if (filters.checkIn.isNotBlank() && filters.checkOut.isNotBlank()) {
        parts.add("${filters.checkIn} – ${filters.checkOut}")
    }
    if (filters.guests != null) parts.add("Guests: ${filters.guests}")
    return parts.joinToString(" · ").ifEmpty { "No filters selected" }
}

/**
 * Displays matching campsite summaries and the current loading or error state.
 *
 * @param searchText The campsite name search entered by the entrant.
 * @param filters The selected search criteria.
 * @param campsites The live list supplied by the search page.
 * @param isLoading Whether the campsite list is loading.
 * @param errorMessage A loading error to display, or null.
 * @param onRetry Reloads the campsite list.
 * @param onBack Returns to the search page.
 * @param onCampsiteClick Opens the campsite identified by its Firestore document ID.
 */
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
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Matching Campsites", color = darkGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            item {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    placeholder = { Text("All campsites") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                        focusedBorderColor = darkGreen, unfocusedBorderColor = cardGray,
                        focusedTextColor = textDark, unfocusedTextColor = textDark,
                        focusedPlaceholderColor = textGray, unfocusedPlaceholderColor = textGray,
                        focusedLeadingIconColor = textGray, unfocusedLeadingIconColor = textGray
                    )
                )
            }
            item {
                Surface(color = cardGray, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Text(describeFilters(filters), color = textDark, fontSize = 13.sp,
                        fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                }
            }
            if (hasDates) {
                item {
                    Text(
                        "Showing campsites available for your full stay. Campsites without availability dates are excluded.",
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
                        Text("${matches.size} ${if (matches.size == 1) "campsite" else "campsites"} found",
                            color = darkGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
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
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp).heightIn(min = 48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = darkGreen, contentColor = Color.White)
        ) {
            Text("Back to Search", fontWeight = FontWeight.Bold)
        }
    }
}

/** Displays the results page title and back button. */
@Composable
private fun ResultsHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(darkGreen)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            .padding(start = 8.dp, end = 16.dp, top = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text("Search Results", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp))
    }
}

/** Displays a clickable search result with its location, capacity, and available rating. */
@Composable
private fun ResultCard(campsite: CampsiteInfo, onClick: () -> Unit) {
    Surface(onClick = onClick, color = cardGray, shape = RoundedCornerShape(8.dp)) {
        Row(Modifier.fillMaxWidth().heightIn(min = 84.dp).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(campsite.name, color = textDark,
                    fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(campsite.location + (campsite.maxOccupancy?.let { " · Up to $it guests" } ?: ""),
                    color = textGray, fontSize = 13.sp)
                if (campsite.rating != null) {
                    Text("★ ${campsite.rating} / 5",
                        color = darkGreen, fontSize = 13.sp)
                }
            }
            Text("›", color = darkGreen, fontSize = 26.sp, modifier = Modifier.padding(start = 12.dp))
        }
    }
}
