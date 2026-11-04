package com.example.quartzevents

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun LotteryDetailScreen (
    onAddEntrant: (Lottery, Entrant) -> Unit,
    lotteries: List<Lottery>,
    lottery: Lottery,
    campsite: String
){
    var back_b by remember {mutableStateOf(false)}
    var next_screen by remember {mutableStateOf(false)}
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
                    back_b = true
                }
            ){
                Icon(
                    painter = painterResource(R.drawable.arrow_back_24px),
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            if(back_b){
                ActiveLotteriesScreen(lotteries,campsite)
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
                modifier = Modifier.fillMaxHeight().padding(top = 25.dp, start = 25.dp),
            ) {
                Text(
                    lottery.name,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                var status: String = ""
                if (lottery.entrants.size == lottery.capacity) {
                    status = "Unavailable Currently"
                } else {
                    status = "Open for Entry"
                }

                Spacer(modifier = Modifier.padding(top = 10.dp))

                Text(
                    "Status: "+status,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Text(
                    "Included Campsites ("+lottery.campsites.size+")",
                    fontSize = 23.sp,
                    color = MaterialTheme.colorScheme.primary
                )


                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(top = 5.dp)
                ) {
                    itemsIndexed(lottery.campsites) { index, campsite ->
                        CampDisplay(
                            modifier = Modifier,
                            campsite = campsite
                        )
                        if (index < lottery.campsites.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Text(
                    "Registration Period",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Box(
                    modifier = Modifier
                        .size(350.dp,50.dp)
                        .padding(top = 5.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(20.dp))
                ){
                    var regDates: String = ""
                    regDates += "Registration: " + months.get(
                        lottery.registrationStart.dateStr().slice(5..6).toInt()
                    ) + " " + lottery.registrationStart.dateStr().slice(8..lottery.registrationStart.dateStr().length - 1)
                        .toInt().toString() + " - " + months.get(
                        lottery.registrationEnd.dateStr().slice(5..6).toInt()
                    ) + " " + lottery.registrationEnd.dateStr().slice(8..lottery.registrationEnd.dateStr().length - 1)
                        .toInt().toString() + ", " + lottery.registrationEnd.dateStr().slice(0..3)
                    Text(
                        regDates,
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
                        .size(350.dp,50.dp)
                        .padding(top = 5.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(20.dp))
                ){
                    var bookDates: String = ""
                    bookDates += "Registration: " + months.get(
                        lottery.bookingStart.dateStr().slice(5..6).toInt()
                    ) + " " + lottery.bookingStart.dateStr().slice(8..lottery.bookingStart.dateStr().length - 1)
                        .toInt().toString() + " - " + months.get(
                        lottery.bookingEnd.dateStr().slice(5..6).toInt()
                    ) + " " + lottery.bookingEnd.dateStr().slice(8..lottery.bookingEnd.dateStr().length - 1)
                        .toInt().toString() + ", " + lottery.bookingEnd.dateStr().slice(0..3)
                    Text(
                        bookDates,
                        fontSize = 20.sp,
                        color = Color.Gray
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        onClick = {
                            next_screen = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            "Join Waiting List",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    if (next_screen) {
                        LotteryStatusScreen({Lottery, Entrant -> Unit},lotteries,lottery,campsite)
                    }
                }
            }
        }
    }
}

@Composable
fun CampDisplay(
    modifier: Modifier,
    campsite: Campsite
) {
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
                   campsite.name,
                    fontSize = 23.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.padding(top = 5.dp))

                Text(
                    campsite.location,
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