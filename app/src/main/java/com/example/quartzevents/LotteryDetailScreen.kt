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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LotteryDetailScreen (
    lottery: Lottery,
    campsite: String = "Pine Ridge"
){
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
                    // Back to previous screen
                }
            ){
                Icon(
                    painter = painterResource(R.drawable.arrow_back_24px),
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
        val temp_sites1: MutableList<Campsite> = mutableListOf(Campsite("Pine Ridge","Hinton, Alberta"), Campsite("Cedar Creek", "Edmonton Area, Alberta"))
        val temp_Lottery_list: MutableList<Lottery> = mutableListOf(Lottery(temp_sites1,"Summer Camping Lottery",Date("2026-10-20"),Date("2026-11-10"),Date("2027-06-01"),Date("2027-06-30")))
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
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(temp_Lottery_list) { index, lottery ->
                        CampsiteLotteryDisplay(
                            modifier = Modifier,
                            lottery = lottery,
                            onClick = {

                            }
                        )
                        if (index < temp_Lottery_list.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}
