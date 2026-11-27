package com.example.quartzevents.EntrantFunctions

// US 01.01.05: reads a campsite by its Firestore document ID and displays its details.

import androidx.compose.runtime.saveable.rememberSaveable
import com.example.quartzevents.LotteryFunctions.ActiveLotteriesScreen
import com.example.quartzevents.LotteryFunctions.LotteryDetailScreen
import com.example.quartzevents.Repositories.LotteryRepository
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.quartzevents.Classes.Date
import com.example.quartzevents.Repositories.CampsiteInfoRepository
import com.example.quartzevents.Repositories.MessageRepo
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

private val darkGreen = Color(0xFF244D3D)
private val backgroundCream = Color(0xFFF7F5ED)
private val cardGray = Color(0xFFD9D9D9)
private val textDark = Color(0xFF303834)
private val textGray = Color(0xFF686868)

/**
 * Loads and displays one campsite, including loading, missing-document, and retry states.
 *
 * @param campsiteId The campsite's Firestore document ID.
 * @param onBack Returns to the page that opened these details.
 */
@Composable
fun CampsiteDetailsScreen(campsiteId: String, entrantId: String? = null, onBack: () -> Unit) {
    var selectedLottery by rememberSaveable(campsiteId, entrantId) { mutableStateOf<String?>(null) }
    var showLotteries by rememberSaveable(campsiteId, entrantId) { mutableStateOf(false) }
    if (selectedLottery != null && entrantId != null) {
        LotteryDetailScreen(selectedLottery!!, entrantId, onBack = { selectedLottery = null })
        return
    }
    if (showLotteries && entrantId != null) {
        ActiveLotteriesScreen(campsiteId, onLotSelect = { selectedLottery = it },
            onLotClick = {}, onBack = { showLotteries = false })
        return
    }
    var campsite by remember(campsiteId) { mutableStateOf<CampsiteInfo?>(null) }
    var isLoading by remember(campsiteId) { mutableStateOf(true) }
    var errorMessage by remember(campsiteId) { mutableStateOf<String?>(null) }
    var reloadCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(campsiteId, reloadCount) {
        isLoading = true
        errorMessage = null
        try {
            campsite = CampsiteInfoRepository.getCampsite(campsiteId)
        } catch (error: FirebaseFirestoreException) {
            errorMessage = "Couldn't load this campsite. Please retry."
        } finally {
            isLoading = false
        }
    }

    BackHandler(onBack = onBack)
    Column(
        modifier = Modifier.fillMaxSize().background(backgroundCream)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
    ) {
        DetailsHeader(onBack)
        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = darkGreen)
            }
            errorMessage != null -> Column(Modifier.padding(20.dp)) {
                Text(errorMessage!!, color = textDark)
                TextButton(onClick = { reloadCount++ }) { Text("Retry", color = darkGreen) }
            }
            campsite == null -> Text("This campsite is no longer available.", color = textDark, modifier = Modifier.padding(20.dp))
            else -> CampsiteDetailsContent(campsite!!, entrantId, onLottery = { selectedLottery = it },
                onViewLotteries = { showLotteries = true })
        }
    }
}

/** Displays the campsite details title and back button. */
@Composable
private fun DetailsHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(darkGreen)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            .padding(start = 8.dp, end = 16.dp, top = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text("Campsite Details", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp))
    }
}

/** Displays campsite information, its photo, and associated open lotteries. */
@Composable
internal fun CampsiteDetailsContent(campsite: CampsiteInfo, entrantId: String? = null, onLottery: (String) -> Unit = {},
                                   onViewLotteries: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(campsite.name, color = textDark, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        CampsitePhoto(campsite.imageUrl)
        Text(campsite.location, color = textDark, fontWeight = FontWeight.Bold)
        if (campsite.price != null) {
            Text("Price: $${campsite.price}/night", color = textDark)
        }
        if (campsite.maxOccupancy != null) {
            Text("Maximum occupancy: ${campsite.maxOccupancy} guests", color = textDark)
        }
        if (campsite.tags.isNotBlank()) Text("Tags: ${campsite.tags}", color = textDark)

        if (campsite.description.isNotBlank()) {
            DetailHeading("About this Campsite")
            Text(campsite.description, color = textDark)
        }

        if (campsite.rules.isNotBlank()) {
            DetailHeading("Campsite Rules")
            Text(campsite.rules, color = textDark)
        }
        if (campsite.cancellationPolicy.isNotBlank()) {
            DetailHeading("Cancellation Policy")
            Text(campsite.cancellationPolicy, color = textDark)
        }

        DetailHeading("Available Lotteries")
        CampsiteLotteries(campsite.id, entrantId, onLottery)
        if (entrantId != null) {
            OutlinedButton(onClick = onViewLotteries) { Text("View campsite lotteries") }
        }
        if (campsite.rating != null) {
            DetailHeading("Overall Rating")
            Text("★ ${campsite.rating} / 5",
                color = darkGreen, fontSize = 18.sp)
        }
    }

}

/** Displays a section heading using the campsite page style. */
@Composable
private fun DetailHeading(text: String) {
    Text(text, color = darkGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
}

/** Shows the campsite photo or the placeholder for listings without an image. */
@Composable
private fun CampsitePhoto(imageUrl: String) {
    Surface(color = cardGray, shape = RoundedCornerShape(8.dp)) {
        Box(Modifier.fillMaxWidth().height(124.dp), contentAlignment = Alignment.Center) {
            if (imageUrl.isNotBlank()) {
                AsyncImage(model = imageUrl, contentDescription = "Campsite photo",
                    contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Image, contentDescription = null, tint = textGray, modifier = Modifier.size(32.dp))
                Text("Campsite photo", color = textGray)
            }
        }
    }
}

/**
 * Checks whether today falls within the lottery registration dates, including both endpoints.
 *
 * @param start Registration start date in YYYY-MM-DD format.
 * @param end Registration end date in YYYY-MM-DD format.
 * @param today Today's date in YYYY-MM-DD format.
 * @return True when the registration dates are valid and include today.
 */
internal fun registrationIsOpen(start: String, end: String, today: String): Boolean {
    val startDate = Date(start)
    val endDate = Date(end)
    val currentDate = Date(today)
    return startDate.isValidDate() && endDate.isValidDate() &&
        !currentDate.isBefore(startDate) && !currentDate.isAfter(endDate)
}

/** Reads the campsite's associated lotteries from Firestore and lists those open for registration. */
@Composable
private fun CampsiteLotteries(campsiteId: String, entrantId: String?, onLottery: (String) -> Unit) {
    var lotteries by remember(campsiteId, entrantId) { mutableStateOf(emptyList<com.example.quartzevents.Classes.Lottery>()) }
    var loading by remember(campsiteId) { mutableStateOf(true) }
    var failed by remember(campsiteId) { mutableStateOf(false) }
    var reloadCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(campsiteId, entrantId, reloadCount) {
        loading = true
        failed = false
        val db = Firebase.firestore
        val campsite = db.collection("campsites").document(campsiteId)
        try {
            val draws = MessageRepo.lotteriesFor(campsite.get(Source.SERVER).await())
            val today = LotteryRepository.today()
            lotteries = draws.map { LotteryRepository.readLottery(it) }.filter {
                registrationIsOpen(it.registrationStart, it.registrationEnd, today) || entrantId in it.entrants
            }
        } catch (error: FirebaseFirestoreException) {
            failed = true
        } finally {
            loading = false
        }
    }
    when {
        loading -> Text("Loading lotteries…", color = textGray)
        failed -> Column {
            Text("Lottery information couldn't be loaded.", color = textGray)
            TextButton(onClick = { reloadCount++ }) { Text("Retry lotteries", color = darkGreen) }
        }
        lotteries.isEmpty() -> Text("No lotteries are currently open for registration.", color = textGray)
        else -> Surface(color = Color(0xFFE3D005), shape = RoundedCornerShape(8.dp)) {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (lottery in lotteries) {
                    if (entrantId == null) Text(lottery.name, color = textDark)
                    else TextButton(onClick = { onLottery(lottery.id!!) }) {
                        Text(lottery.name, color = darkGreen)
                    }
                }
            }
        }
    }
}
