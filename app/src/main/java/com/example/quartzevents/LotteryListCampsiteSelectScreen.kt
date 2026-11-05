package com.example.quartzevents

import android.R.attr.checked
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
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LotteryListCampsiteSelectScreen(
    lotteries: List<Lottery>, // lotteries list - connects to db
    campsites: MutableList<Campsite> // Sites explicitly owned by the Owner
){
    var back_b by remember {mutableStateOf(false)} // used in button presses for changing screens
    var next_screen by remember {mutableStateOf(false)}
    var camp_info: MutableList<MutableMap<String,MutableList<Date>>> = mutableListOf<MutableMap<String,MutableList<Date>>>()

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
                    back_b = true
                }
            ){
                Icon(
                    painter = painterResource(R.drawable.arrow_back_24px),
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            if (back_b) {
                // previous screen function goes here
            }

            Spacer(modifier = Modifier.padding(15.dp))

            Text(
                "Lottery List",
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
                    "Campsites in a Lottery",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                for (lottery in lotteries) {
                    for (site in lottery.campsites) {
                        if (site in campsites) { // ensures a site is only listed if the owner owns it
                            camp_info.add(
                                mutableMapOf(
                                    site.name to mutableListOf(
                                        lottery.registrationStart,
                                        lottery.registrationEnd
                                    )
                                )
                            )
                        }
                    }
                }

                LazyColumn( // List of available Lottery displays
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(camp_info) { index, site_info ->
                        CampsiteListing( // call composable function that builds each lottery display
                            site_info = site_info
                        )
                        if (index < camp_info.lastIndex) {
                            HorizontalDivider()
                        }
//                        if(next_screen) {
//                            LotteryDetailScreen({Lottery, Entrant -> Unit},lotteries,lottery,campsite)
//                        }
                    }
                }
                Column(
                    modifier = Modifier.padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(  // Edit Button
                            onClick = {
                                // TODO: Add US 02.04.07 functionality here
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.DarkGray
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                "Edit",
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        Button( // Remove Button
                            onClick = {
                                // TODO: Add delete functionality (removing campsites from lotteries)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFC46A81)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                "Remove",
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    Button(
                        onClick = {
                            next_screen = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            "Create new Lottery",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    if (next_screen){
                        // next screen here
                    }
                }
            }
        }
    }
}

@Composable
fun CampsiteListing (
    site_info: MutableMap<String,MutableList<Date>>
) {
    var isChecked by remember { mutableStateOf(false)}
    val key = site_info.keys.toList()[0]
    val dates = site_info[key]
    Row (
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { isChecked = it },
        )
        Box (
            modifier = Modifier
                .size(300.dp, 100.dp)
                .padding(start = 5.dp)
                .background(color = Color.LightGray, shape = RoundedCornerShape(20.dp))
        ) {
            Column (
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    key,
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(5.dp)
                )
                Text(
                    "From: "+ dates?.get(0)?.dateStr(),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp,bottom = 5.dp)
                )

                Text(
                    "To: "+ dates?.get(1)?.dateStr(),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start =8.dp, bottom = 5.dp)
                )
            }
        }
    }
}