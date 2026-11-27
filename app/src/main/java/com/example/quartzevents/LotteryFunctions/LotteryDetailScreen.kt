package com.example.quartzevents.LotteryFunctions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quartzevents.Classes.Campsite
import com.example.quartzevents.Classes.Entrant
import com.example.quartzevents.Classes.Lottery
import com.example.quartzevents.R
import com.example.quartzevents.Repositories.CampsiteInfoRepository
import com.example.quartzevents.Repositories.LotteryRepository

@Composable
fun LotteryDetailScreen (
    lotteryId: String,
    userId: String,
    onAddClick: () -> Unit,
    onBack: () -> Unit
) {
    val lotRepo = remember { LotteryRepository() }
    val siteRepo = remember { CampsiteInfoRepository() }
    DisposableEffect(lotRepo, siteRepo) {
        onDispose {
            lotRepo.stopListening()
            siteRepo.stopListening()
        }
    }
    BackHandler(onBack = onBack)
    val lottery = lotRepo.getAllLotteries().firstOrNull { it.id == lotteryId }
    if (lottery == null) {
        Text(lotRepo.errorMessage ?: if (lotRepo.isLoading) "Loading lottery..." else "Lottery unavailable.")
        androidx.compose.material3.TextButton(onClick = onBack, modifier = Modifier.padding(top = 32.dp)) { Text("Back") }
        return
    }
    val scope = rememberCoroutineScope()
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val joined = userId in lottery.entrants
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 45.dp)
        ) {
            IconButton(
                modifier = Modifier.padding(start = 15.dp),
                onClick = onBack
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }



                Spacer(modifier = Modifier.padding(15.dp))
                Text(
                    "Lottery Details",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(top = 25.dp, start = 25.dp, bottom = 20.dp),
                ) {
                    Text(
                        lottery.name,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    val status = when {
                        !lottery.registrationOpen() -> "Registration closed"
                        lottery.isWaitlistFull() -> "Unavailable Currently"
                        else -> "Open for Entry"
                    }
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Text(
                        "Status: " + status,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.padding(top = 20.dp))
                    Text(
                        "Included Campsites (" + lottery.campsites.size + ")",
                        fontSize = 23.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    val includedSites = siteRepo.campsites.filter { it.id in lottery.campsites }
                    includedSites.forEachIndexed { index, site ->
                        CampDisplay(name = site.name, location = site.location)
                        if (index < includedSites.lastIndex) HorizontalDivider()
                    }
                    Spacer(modifier = Modifier.padding(top = 20.dp))
                    Text(
                        "Registration Period",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Box(
                        modifier = Modifier
                            .size(350.dp, 50.dp)
                            .padding(top = 5.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(20.dp))
                    ) {
                        Text(
                            "Registration: " + lottery.registrationStart + " - " + lottery.registrationEnd,
                            fontSize = 20.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.padding(top = 15.dp))
                    Text(
                        "Booking Period",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Box(
                        modifier = Modifier
                            .size(350.dp, 50.dp)
                            .padding(top = 5.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(20.dp))
                    ) {
                        Text(
                            "Booking: " + lottery.bookingStart + " - " + lottery.bookingEnd,
                            fontSize = 20.sp,
                            color = Color.Gray
                        )
                    }
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            onClick = {
                                if (joined) {
                                    onAddClick()
                                } else {
                                    saving = true
                                    error = null
                                    scope.launch {
                                        try {
                                            lotRepo.addEntrant(lottery, Entrant(uid = userId)).await()
                                            onAddClick()
                                        } catch (e: CancellationException) {
                                            throw e
                                        } catch (e: Exception) {
                                            error = e.localizedMessage ?: "Could not join the waiting list."
                                        } finally {
                                            saving = false
                                        }
                                    }
                                }
                            },
                            enabled = !saving && (joined || (lottery.registrationOpen() && !lottery.isWaitlistFull())),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                if (joined) "View My Entry" else "Join Waiting List",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
}

@Composable
fun CampDisplay(
    campsite: Campsite
) {
    CampDisplay(campsite.name, campsite.location)
}

@Composable
private fun CampDisplay(name: String, location: String) {
    Box (
        modifier = Modifier
            .size(350.dp,65.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(20.dp))
    ) {
        Row (
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    name,
                    fontSize = 23.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.padding(top = 5.dp))
                Text(
                    location,
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
            Icon(
                painter = painterResource(R.drawable.chevron_right),
                contentDescription = "arrow icon",
                modifier = Modifier.padding(start = 5.dp),
                tint = Color.Gray
            )
        }
    }
}