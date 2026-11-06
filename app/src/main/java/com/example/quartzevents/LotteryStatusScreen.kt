package com.example.quartzevents

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import androidx.compose.material3.OutlinedButton

@Composable
fun LotteryStatusScreen (
    onDeleteEntrant: (Lottery, Entrant) -> Unit,
    lotteries: List<Lottery>,
    lottery: Lottery,
    campsite: String,
    user: Entrant
){
    var backB by remember {mutableStateOf(false)}
    val months: MutableMap<Int, String> = mutableMapOf(
        Pair(1,"Jan"), Pair(2,"Feb"), Pair(3,"Mar"), Pair(4,"Apr"), Pair(5,"May"), Pair(6,"Jun"), Pair(7,"Jul"), Pair(8,"Aug"), Pair(9,"Sep"), Pair(10,"Oct"), Pair(11,"Nov"), Pair(12,"Dec") )
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
            IconButton(
                modifier = Modifier.padding(start = 15.dp),
                onClick = {
                    backB = true
                }
            ){
                Icon(
                    painter = painterResource(R.drawable.arrow_back_24px),
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            if(backB){
                LotteryDetailScreen({lottery, user -> Unit},lotteries,lottery,campsite, user)
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

                var sites = ""
                var i = 0
                for (i in 0..<(lottery.campsites.size-1)) {
                    sites += lottery.campsites[i].location+" / "
                }
                sites += lottery.campsites[lottery.campsites.size-1].location

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
                        "Joined Waiting List",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(5.dp)
                    )


                }

                Text(
                    "Draw Date: "+months.get(
                        lottery.registrationEnd.dateStr().slice(5..6).toInt()
                    ) + " " + lottery.registrationEnd.dateStr().slice(8..lottery.registrationEnd.dateStr().length - 1)
                        .toInt().toString() + ", " + lottery.registrationEnd.dateStr().slice(0..3),
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

        val instant = Instant.now()
        val currentTime = Date(instant.toString())
        if(lottery.registrationEnd.isEqual(currentTime) or lottery.registrationEnd.isAfter(currentTime)) {
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
                    "Waiting",
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
                    "Finished",
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

        Spacer(modifier = Modifier.padding(50.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            OutlinedButton( // referenced https://kotlinlang.org/api/compose-multiplatform/material3/androidx.compose.material3/-outlined-button.html for implementation
                onClick = {
                    onDeleteEntrant(lottery, user)
                },
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