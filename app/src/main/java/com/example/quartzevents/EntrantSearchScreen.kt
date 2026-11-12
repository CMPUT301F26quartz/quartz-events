package com.example.quartzevents

// US 01.01.01: loads campsites and connects search, filters, results, and campsite details.
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val darkGreen = Color(0xFF244D3D)
private val backgroundCream = Color(0xFFF7F5ED)
private val cardGray = Color(0xFFD9D9D9)
private val textDark = Color(0xFF303834)
private val textGray = Color(0xFF686868)
private val pagePadding = 18.dp
private val itemSpacing = 12.dp
private val cornerRadius = 8.dp

private enum class SearchPage { SEARCH, FILTERS, RESULTS }

/**
 * Displays the live campsite list and switches between search, filters, results, and details.
 *
 * @param onBack Returns to the entrant home page.
 */
@Composable
fun EntrantSearchScreen(onBack: () -> Unit) {
    var searchText by remember { mutableStateOf("") }
    var filters by remember { mutableStateOf(CampsiteFilters()) }
    var page by remember { mutableStateOf(SearchPage.SEARCH) }
    var selectedCampsiteId by remember { mutableStateOf<String?>(null) }
    var reloadCount by remember { mutableIntStateOf(0) }
    val repository = remember(reloadCount) { CampsiteInfoRepository() }
    val campsites = repository.campsites
    val isLoading = repository.isLoading
    val errorMessage = repository.errorMessage

    DisposableEffect(repository) {
        // Stop the old listener when leaving this screen or retrying.
        onDispose {
            repository.stopListening()
        }
    }

    val campsiteId = selectedCampsiteId
    if (campsiteId != null) {
        CampsiteDetailsScreen(campsiteId, onBack = { selectedCampsiteId = null }, repository = repository)
        return
    }

    if (page == SearchPage.FILTERS) {
        EntrantFilterScreen(
            initialFilters = filters,
            onBack = { page = SearchPage.SEARCH },
            onApply = { selectedFilters ->
                filters = selectedFilters
                page = SearchPage.RESULTS
            }
        )
        return
    }

    if (page == SearchPage.RESULTS) {
        EntrantSearchResultsScreen(
            searchText = searchText.trim(),
            filters = filters,
            campsites = campsites,
            isLoading = isLoading,
            errorMessage = errorMessage,
            onRetry = { reloadCount++ },
            onBack = { page = SearchPage.SEARCH },
            onCampsiteClick = { selectedCampsiteId = it }
        )
        return
    }

    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundCream)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
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
                onTextChange = { searchText = it },
                onFilters = { page = SearchPage.FILTERS }
            )

            if (!filters.isEmpty) {
                Text(describeFilters(filters), color = textDark)
                Spacer(modifier = Modifier.height(itemSpacing))
            }

            Text(
                text = "Available Campsites",
                color = darkGreen,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(itemSpacing))

            when {
                isLoading -> Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = darkGreen)
                }
                errorMessage != null -> Column(Modifier.weight(1f)) {
                    Text(errorMessage!!, color = textDark)
                    TextButton(onClick = { reloadCount++ }) { Text("Retry", color = darkGreen) }
                }
                else -> CampsiteList(
                    campsites = campsites,
                    onCampsiteClick = { selectedCampsiteId = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = { page = SearchPage.RESULTS },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(cornerRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = darkGreen,
                    contentColor = Color.White
                )
            ) { Text("Search", fontWeight = FontWeight.Bold) }
        }
    }
}

/** Displays the search page title and back button. */
@Composable
private fun SearchHeader(onBack: () -> Unit) {
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
            text = "Search Campsites",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

/** Displays the campsite name input and the button that opens the filters. */
@Composable
private fun SearchControls(
    searchText: String,
    onTextChange: (String) -> Unit,
    onFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 24.dp)
    ) {
        Text("Find a Campsite", color = darkGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(
            value = searchText,
            onValueChange = onTextChange,
            placeholder = { Text("Search campsites...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("campsiteSearchInput"),
            shape = RoundedCornerShape(cornerRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = darkGreen,
                unfocusedBorderColor = cardGray,
                focusedTextColor = textDark,
                unfocusedTextColor = textDark,
                focusedPlaceholderColor = textGray,
                unfocusedPlaceholderColor = textGray,
                focusedLeadingIconColor = textGray,
                unfocusedLeadingIconColor = textGray,
                cursorColor = darkGreen
            )
        )

        Spacer(modifier = Modifier.height(itemSpacing))

        TextButton(
            onClick = onFilters,
            modifier = Modifier.align(Alignment.End).width(104.dp),
            shape = RoundedCornerShape(cornerRadius),
            colors = ButtonDefaults.textButtonColors(containerColor = cardGray, contentColor = textDark)
        ) { Text("Filters", fontWeight = FontWeight.Bold) }
    }
}

/** Displays campsite cards and passes the selected document ID to [onCampsiteClick]. */
@Composable
private fun CampsiteList(
    campsites: List<CampsiteInfo>,
    onCampsiteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        if (campsites.isEmpty()) {
            item { Text("No campsites found.", color = textGray) }
        }

        items(campsites, key = { it.id }) { campsite ->
            CampsiteCard(campsite = campsite, onClick = { onCampsiteClick(campsite.id) })
        }
    }
}

/** Displays a clickable campsite summary with its name, location, and tags. */
@Composable
private fun CampsiteCard(campsite: CampsiteInfo, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = cardGray,
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Row(Modifier.fillMaxWidth().heightIn(min = 84.dp).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(campsite.name, color = textDark, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(listOf(campsite.location, campsite.tags).filter { it.isNotBlank() }.joinToString(" · "),
                    color = textGray, fontSize = 13.sp)
            }
            Text("›", color = darkGreen, fontSize = 26.sp, modifier = Modifier.padding(start = 12.dp))
        }
    }
}
