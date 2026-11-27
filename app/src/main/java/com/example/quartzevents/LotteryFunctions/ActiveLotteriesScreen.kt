package com.example.quartzevents.LotteryFunctions


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.quartzevents.Classes.Lottery
import com.example.quartzevents.R
import androidx.compose.runtime.DisposableEffect
import com.example.quartzevents.Repositories.CampsiteInfoRepository
import com.example.quartzevents.EntrantFunctions.registrationIsOpen
import com.example.quartzevents.Repositories.LotteryRepository

@Composable
fun ActiveLotteriesScreen(
    campsite: String,
    onLotSelect: (String?) -> Unit,
    onLotClick: () -> Unit,
    onBack: () -> Unit
){
    val lotRepo = remember { LotteryRepository() }
    val siteRepo = remember { CampsiteInfoRepository() }
    DisposableEffect(Unit) { onDispose { lotRepo.stopListening(); siteRepo.stopListening() } }
    val lotteries = lotRepo.getAllLotteries().filter { campsite in it.campsites }
    val siteNames = siteRepo.campsites.associate { it.id to it.name }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ){
        Row (
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 45.dp)
        ){
            IconButton( // back button, referenced https://developer.android.com/develop/ui/compose/components/icon-button for implementation
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
                "Active Lotteries",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxHeight().padding(top = 25.dp, start = 25.dp),
            ) {
                Text(
                    "Available Lotteries",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.padding(top = 10.dp))

                Text(
                    "For: " + (siteNames[campsite] ?: campsite),
                    fontSize = 20.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                when {
                    lotRepo.isLoading -> Text("Loading lotteries...")
                    lotRepo.errorMessage != null -> Text(lotRepo.errorMessage!!)
                    lotteries.isEmpty() -> Text("No lotteries linked to this campsite.")
                }
                LazyColumn( // List of available Lottery displays
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(lotteries) { index, lottery ->
                        CampsiteLotteryDisplay( // call composable function that builds each lottery display
                            lottery = lottery,
                            campsiteNames = lottery.campsites.mapNotNull(siteNames::get),
                            onClick = {
                                onLotSelect(lottery.id)
                                onLotClick()
                            }
                        )
                        if (index < lotteries.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
    BackHandler(onBack = onBack)
}

@Composable
fun CampsiteLotteryDisplay ( // Framework for the Box displays of Lottery Info
    lottery: Lottery,
    onClick: () -> Unit
) {
    val repo = remember { CampsiteInfoRepository() }
    DisposableEffect(repo) { onDispose { repo.stopListening() } }
    CampsiteLotteryDisplay(lottery, repo.campsites.filter { it.id in lottery.campsites }.map { it.name }, onClick)
}

@Composable
private fun CampsiteLotteryDisplay(
    lottery: Lottery,
    campsiteNames: List<String>,
    onClick: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {onClick()}
            .background(color = Color.LightGray,shape = RoundedCornerShape(20.dp)),
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(Color.LightGray)
                    .padding(all = 10.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,

                ) {
                Text(
                    lottery.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                val today = LotteryRepository.today()
                val status = when {
                    lottery.drawnAt != null || lottery.winners.isNotEmpty() -> "Draw complete"
                    today < lottery.registrationStart -> "Registration upcoming"
                    !registrationIsOpen(lottery.registrationStart, lottery.registrationEnd, today) -> "Registration closed"
                    lottery.isWaitlistFull() -> "Waitlist full"
                    else -> "Open for Entry"
                }
                Text(
                    status,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    campsiteNames.joinToString(),
                    fontSize = 15.sp,
                    color = Color.Gray
                )
                Text(
                    "Registration: " + lottery.registrationStart + " - " + lottery.registrationEnd,
                    fontSize = 15.sp,
                    color = Color.Gray
                )

            }
            Icon( // Create arrow icon
                painter = painterResource(R.drawable.chevron_right),
                contentDescription = "arrow icon",
                modifier = Modifier.padding(start = 5.dp),
                tint = Color.Gray
            )
        }
    }
}