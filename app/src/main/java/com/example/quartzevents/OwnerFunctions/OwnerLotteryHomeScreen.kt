/**
 * Displays all lotteries for the user to browse, and they can choose to edit
 * a lottery they are the creator of, or add one or more of their campsites
 * to a lottery they do not own. There is also an option to initiate the lottery
 * creation process.
 */

package com.example.quartzevents.OwnerFunctions

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
import com.example.quartzevents.Classes.Campsite
import com.example.quartzevents.Classes.Lottery
import com.example.quartzevents.R
import com.example.quartzevents.Repositories.CampsiteRepoMain
import com.example.quartzevents.Repositories.LotteryRepository
import com.example.quartzevents.model.UserProfile

@Composable
fun OwnerLotteryHomeScreen(
    profile: UserProfile,
    onCreateLottery: () -> Unit,
    onBack: () -> Unit,
    onLotterySelect: (String?) -> Unit,
    onUpdateLottery: () -> Unit
){
    val campRepo = remember { CampsiteRepoMain() }
    val campsites = campRepo.getAllCamsites()
    val lotRepo = remember { LotteryRepository() }
    val lotteries = lotRepo.getAllLotteries()
    var campInfo: MutableList<MutableMap<Pair<String,String>,MutableList<String>>> = mutableListOf<MutableMap<Pair<String,String>,MutableList<String>>>()
    var selectedLot by remember {mutableStateOf(Lottery())}

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
                    "Lotteries and Campsites",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))


                LazyColumn( // List of available Lottery displays
                    modifier = Modifier.fillMaxSize().weight(1f)
                ) {
                    itemsIndexed(lotteries) { index, lottery ->
                        LotteryListing( // call composable function that builds each lottery display
                            lottery = lottery,
                            onAddSite = {selectedLot = it},
                            onRemoveSite = {selectedLot =
                                Lottery()
                            },
                            selectedLot = selectedLot,
                        )
                        if (index < campInfo.lastIndex) {
                            HorizontalDivider()
                        }

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
                                if(profile.uid == selectedLot.creatorId) {
                                    onLotterySelect (selectedLot.id)
                                    onUpdateLottery()
                                } else {
                                    onLotterySelect (selectedLot.id)
                                    onUpdateLottery()
                                }
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
                            onCreateLottery()
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
                }
            }
        }
    }
    BackHandler (onBack = onBack)
}


@Composable
fun LotteryListing (
    lottery: Lottery,
    onAddSite: (Lottery) -> Unit,
    onRemoveSite: (Lottery) -> Unit,
    selectedLot: Lottery,
) {
    val campRepo = remember { CampsiteRepoMain() }
    val dates = mutableListOf(lottery.registrationStart, lottery.registrationEnd)
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Checkbox(
            checked = selectedLot.id == lottery?.id,
            onCheckedChange = { checked ->
                if (checked) {
                    lottery?.let {
                        onAddSite(it)
                    }
                } else {
                    onRemoveSite(selectedLot)
                }
            }
        )
        Box(
            modifier = Modifier
                .size(300.dp, 100.dp)
                .padding(start = 5.dp)
                .background(color = Color.LightGray, shape = RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    lottery.name,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(7.dp)
                )
                var campsiteString = ""
                lottery.campsites.forEach { site ->
                    val campsite = campRepo.getCampsite(site)
                    if (campsiteString == "")
                        campsiteString += campsite?.name
                    else
                        campsiteString += " / " + campsite?.name
                    Text(
                        campsiteString,
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(5.dp)
                    )
                    Text(
                        "From: " + dates?.get(0),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp, bottom = 5.dp)
                    )

                    Text(
                        "To: " + dates?.get(1),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }
}