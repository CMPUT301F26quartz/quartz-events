package com.example.quartzevents.LotteryFunctions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.OutlinedButton
import com.example.quartzevents.Classes.Campsite
import com.example.quartzevents.Classes.Date
import com.example.quartzevents.Classes.Entrant
import com.example.quartzevents.Classes.Lottery
import com.example.quartzevents.R
import com.example.quartzevents.Repositories.CampsiteInfoRepository
import com.example.quartzevents.Repositories.LotteryRepository

@Composable
fun LotteryStatusScreen (
    lotteryId: String,
    userId: String,
    onBack: () -> Unit
){
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
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.secondary)
    ){
        Row (
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 45.dp)
        ){
            IconButton(
                modifier = Modifier.padding(start = 15.dp),
                onClick = onBack
            ){
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.padding(15.dp))

            Text(
                "Lottery Status",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.padding(top = 5.dp))

        Text(
            "My Lottery Entry",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(20.dp)
        )

        Box(
            modifier = Modifier
                .size(350.dp,150.dp)
                .padding(start = 20.dp)
                .background(color = Color.LightGray,shape = RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxHeight().padding(10.dp)
            ) {
                Text(
                    lottery.name,
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.padding(top = 5.dp))

                val sites = siteRepo.campsites.filter { it.id in lottery.campsites }.joinToString(" / ") { it.location }

                Text(
                    sites,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Box (
                    modifier = Modifier
                        .size(140.dp,40.dp)
                        .padding(5.dp)
                        .background(Color(0xFFBAFCA4), shape = RoundedCornerShape(20.dp))
                ) {
                    Text(
                        if (joined) "Joined Waiting List" else "Not on Waiting List",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(5.dp)
                    )


                }

                Text(
                    "Registration ends: " + lottery.registrationEnd,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Text(
            "Entry Details",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp, top = 25.dp)
        )

        if (lottery.drawnAt == null && lottery.winners.isEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Entry Status",
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(20.dp)
                )

                Spacer(modifier = Modifier.padding(start = 160.dp))

                Text(
                    if (joined) "Waiting" else "Not entered",
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
            Text(
                "You Will be Notified When the Lottery Ends.",
                fontSize = 17.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(start = 40.dp, top = 10.dp)
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Entry Status",
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(20.dp)
                )

                Spacer(modifier = Modifier.padding(80.dp))

                Text(
                    if (userId in lottery.winners) "Selected" else "Not selected",
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
            Text(
                "Check Your Notifications to See if You Have Won.",
                fontSize = 17.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(start = 40.dp, top = 10.dp)
            )
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(modifier = Modifier.padding(50.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            OutlinedButton( // referenced https://kotlinlang.org/api/compose-multiplatform/material3/androidx.compose.material3/-outlined-button.html for implementation
                onClick = {
                    saving = true
                    error = null
                    scope.launch {
                        try {
                            lotRepo.deleteEntrant(lottery, Entrant(uid = userId)).await()
                            onBack()
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            error = e.localizedMessage ?: "Could not leave the waiting list."
                        } finally {
                            saving = false
                        }
                    }
                },
                enabled = joined && lottery.registrationOpen() && !saving,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    "Leave Waiting List",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}