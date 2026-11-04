package com.example.quartzevents

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Shared styles for this screen.
// TODO: Use the app's shared theme when it is ready.
private val darkGreen = Color(0xFF234F40)
private val backgroundCream = Color(0xFFF8F6ED)
private val cardGray = Color(0xFFD9D9D9)
private val textDark = Color(0xFF303834)
private val textGray = Color(0xFF686868)

private val pagePadding = 20.dp
private val itemSpacing = 12.dp
private val cornerRadius = 8.dp

// Temporary model for displaying sample campsites.
private data class SampleCampsite(
    val name: String,
    val description: String
)

// TODO: Replace these samples with data from the campsite repository.
private val sampleCampsites = listOf(
    SampleCampsite(
        name = "Pine Ridge Campground",
        description = "Near Edmonton · Forest campsite"
    ),
    SampleCampsite(
        name = "Cedar Creek Campsite",
        description = "Alberta · Riverside campsite"
    ),
    SampleCampsite(
        name = "Aspen Lake Campground",
        description = "Alberta · Lakeside campsite"
    )
)

// MainActivity handles returning to the home screen.
@Composable
fun EntrantSearchScreen(onBack: () -> Unit) {
    var searchText by rememberSaveable {
        mutableStateOf("")
    }

    // Only update the results when the Search button is pressed.
    var submittedSearch by rememberSaveable {
        mutableStateOf("")
    }

    val matchingCampsites = sampleCampsites.filter { campsite ->
        campsite.name.contains(
            other = submittedSearch,
            ignoreCase = true
        )
    }

    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundCream)
            .safeDrawingPadding()
            .imePadding()
    ) {
        SearchHeader(onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = pagePadding)
        ) {
            SearchControls(
                searchText = searchText,
                onTextChange = { newText ->
                    searchText = newText
                }
            )

            Text(
                text = "Available Campsites",
                color = darkGreen,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Sample data",
                color = textGray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(itemSpacing))

            // The list takes the remaining space above the Search button.
            CampsiteList(
                campsites = matchingCampsites,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    submittedSearch = searchText.trim()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(cornerRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = darkGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Search",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Top bar: back button and page title.
@Composable
private fun SearchHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(darkGreen)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.size(48.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color.White
            )
        ) {
            Text(
                text = "←",
                fontSize = 45.sp,
                modifier = Modifier.offset(y = (-15).dp)
            )
        }

        Text(
            text = "Search Campsites",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

// Search input and filter button.
@Composable
private fun SearchControls(
    searchText: String,
    onTextChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        Text(
            text = "Find a Campsite",
            color = darkGreen,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(itemSpacing))

        OutlinedTextField(
            value = searchText,
            onValueChange = onTextChange,
            placeholder = {
                Text(text = "Search campsites...")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(cornerRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = textDark,
                unfocusedTextColor = textDark,
                focusedPlaceholderColor = textGray,
                unfocusedPlaceholderColor = textGray,
                focusedBorderColor = darkGreen,
                unfocusedBorderColor = cardGray,
                cursorColor = darkGreen
            )
        )

        Spacer(modifier = Modifier.height(itemSpacing))

        // TODO: Add location, date, and guest filters.
        Button(
            onClick = {},
            enabled = false,
            modifier = Modifier.align(Alignment.End),
            shape = RoundedCornerShape(cornerRadius),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = cardGray,
                disabledContentColor = textDark
            )
        ) {
            Text(
                text = "Filters",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Scrollable results, including an empty state.
@Composable
private fun CampsiteList(
    campsites: List<SampleCampsite>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        if (campsites.isEmpty()) {
            item {
                Text(
                    text = "No campsites found.",
                    color = textGray
                )
            }
        }

        items(campsites) { campsite ->
            CampsiteCard(campsite = campsite)
        }
    }
}

// One campsite in the results list.
@Composable
private fun CampsiteCard(campsite: SampleCampsite) {
    // TODO: Add a click callback using the shared campsite ID.
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = cardGray,
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = campsite.name,
                    color = textDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = campsite.description,
                    color = textGray,
                    fontSize = 13.sp
                )
            }

            Text(
                text = "›",
                color = darkGreen,
                fontSize = 26.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}
