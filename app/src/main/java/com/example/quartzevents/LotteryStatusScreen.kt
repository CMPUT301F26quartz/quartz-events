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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LotteryStatusScreen (
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
                LotteryDetailScreen(lottery,campsite)
            }

            Spacer(modifier = Modifier.padding(15.dp))

            Text(
                "Lottery Status",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.padding(top = 25.dp))

        Text(
            "My Lottery Entry",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.padding(top = 10.dp))

        Box(
            modifier = Modifier
                .size(350.dp,100.dp)
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
                    color = Color.Gray
                )

                Box (
                    modifier = Modifier
                        .size(70.dp,15.dp)
                        .padding(5.dp)
                        .background(Color(0xFFBAFCA4))
                ) {
                    Text(
                        "Joined Waiting List",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.padding(14.dp))

                    Text(
                        "Draw Date: "+months.get(
                            lottery.registrationEnd.date.slice(5..6).toInt()
                        ) + " " + lottery.registrationEnd.date.slice(8..lottery.registrationEnd.date.length - 1)
                            .toInt().toString() + ", " + lottery.registrationEnd.date.slice(0..3),
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            }
        }
    }
}



@Preview
@Composable
fun PreviewLotteryStatusScreen (
    lottery: Lottery = Lottery(mutableListOf(Campsite("camp","AB")), "test", Date("2026-07-20"),Date("2026-08-09"),Date("2027-03-02"),Date("2027-04-09"),100),
    campsite: String = "camp"
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
                LotteryDetailScreen(lottery,campsite)
            }

            Spacer(modifier = Modifier.padding(15.dp))

            Text(
                "Lottery Status",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.padding(top = 25.dp))

        Text(
            "My Lottery Entry",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.padding(top = 10.dp))

        Box(
            modifier = Modifier
                .size(350.dp,100.dp)
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
                    color = Color.Gray
                )

                Box (
                    modifier = Modifier
                        .size(70.dp,15.dp)
                        .padding(5.dp)
                        .background(Color(0xFFBAFCA4))
                ) {
                    Text(
                        "Joined Waiting List",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.padding(14.dp))

                    Text(
                        "Draw Date: "+months.get(
                            lottery.registrationEnd.date.slice(5..6).toInt()
                        ) + " " + lottery.registrationEnd.date.slice(8..lottery.registrationEnd.date.length - 1)
                            .toInt().toString() + ", " + lottery.registrationEnd.date.slice(0..3),
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            }
        }
    }
}
