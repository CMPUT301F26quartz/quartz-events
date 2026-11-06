package com.example.quartzevents


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

@Composable
fun ActiveLotteriesScreen(
    lotteries: List<Lottery>, // lotteries list - connects to db
    campsite: String,
    user: Entrant
){
    var backB by remember {mutableStateOf(false)} // used in button presses for changing screens
    var nextScreen by remember {mutableStateOf(false)}

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
            IconButton( // back button
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

            if (backB) {
                // previous screen function goes here
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
                    "For: " + campsite,
                    fontSize = 20.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                LazyColumn( // List of available Lottery displays
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(lotteries) { index, lottery ->
                        CampsiteLotteryDisplay( // call composable function that builds each lottery display
                            lottery = lottery,
                            onClick = {
                                nextScreen = true
                            }
                        )
                        if (index < lotteries.lastIndex) {
                            HorizontalDivider()
                        }
                        if(nextScreen) {
                            LotteryDetailScreen({lottery, user -> Unit},lotteries,lottery,campsite, user)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CampsiteLotteryDisplay ( // Framework for the Box displays of Lottery Info
    lottery: Lottery,
    onClick: () -> Unit
){
    val months: MutableMap<Int, String> = mutableMapOf( // For use in converting Dates to more readable forms
        Pair(1,"Jan"), Pair(2,"Feb"), Pair(3,"Mar"), Pair(4,"Apr"), Pair(5,"May"), Pair(6,"Jun"), Pair(7,"Jul"), Pair(8,"Aug"), Pair(9,"Sep"), Pair(10,"Oct"), Pair(11,"Nov"), Pair(12,"Dec")
    )
    Box(
        modifier = Modifier
            .size(350.dp,110.dp)
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
                var status: String = ""
                var sites: String = ""
                lottery.campsites.forEach { site ->
                    sites += site.name + "  "
                }
                if (lottery.entrants.size == lottery.capacity) { // Show unavailable if the lottery capacity is hit
                    status = "Unavailable Currently"
                } else {
                    status = "Open for Entry"
                }
                Text(
                    status,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    sites,
                    fontSize = 15.sp,
                    color = Color.Gray
                )
                var regDates: String = ""
                regDates += "Registration: " + months.get( // puts Registration dates into a "Month day - Month day, year" format
                    lottery.registrationStart.date.slice(5..6).toInt()
                ) + " " + lottery.registrationStart.date.slice(8..lottery.registrationStart.date.length - 1)
                    .toInt().toString() + " - " + months.get(
                    lottery.registrationEnd.date.slice(5..6).toInt()
                ) + " " + lottery.registrationEnd.date.slice(8..lottery.registrationEnd.date.length - 1)
                    .toInt().toString() + ", " + lottery.registrationEnd.date.slice(0..3)
                Text(
                    regDates,
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