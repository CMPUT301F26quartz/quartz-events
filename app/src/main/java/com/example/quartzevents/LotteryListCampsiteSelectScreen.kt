/**
 * Displays to the Owner Campsites they own that are part of Lotteries,
 * and what the registration periods of those lotteries are.
 *
 * Each Campsite Display includes the name of the campsite and the registration
 * start and end dates for the lottery it is a part of. These displays are contained
 * inside a lazy column for ease of display in the UI. There are three buttons at the
 * of the screen. The "create new lottery" button calls the LotteryCreationScreen1() function
 * from LotteryCreationScreen1.kt, and is the first step in the lottery creation/registration
 * process. The "Edit" button calls the LotteryUpdateScreen1() function
 * from LotteryUpdateScreen1.kt, and is the first step in the lottery update process.
 * The "delete" button does not yet have functionality, but will lead to the deletion of the
 * lottery associated with the campsite selected.
 */

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
import androidx.compose.runtime.mutableStateListOf
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

/**
 * The main UI body, contains all UI elements, including the list of CampsiteListing objects
 *
 * Each Campsite Display includes the name of the campsite and the registration
 *  * start and end dates for the lottery it is a part of. These displays are contained
 *  * inside a lazy column for ease of display in the UI. There are three buttons at the
 *  * of the screen. The "create new lottery" button calls the LotteryCreationScreen1() function
 *  * from LotteryCreationScreen1.kt, and is the first step in the lottery creation/registration
 *  * process. The "Edit" button calls the LotteryUpdateScreen1() function
 *  * from LotteryUpdateScreen1.kt, and is the first step in the lottery update process.
 *  * The "delete" button does not yet have functionality, but will lead to the deletion of the
 *  * lottery associated with the campsite selected.
 *
 *  @property lotteries a list of Lottery objects.
 *  @property campsites a mutable list of Campsite objects
 *  @property onAddLottery a lambda function to be passed to another function
 *  @property onUpdateLottery a lambda function to be passed to another function
 */
@Composable
fun LotteryListCampsiteSelectScreen(
    lotteries: List<Lottery>, // lotteries list - connects to db
    campsites: List<Campsite>, // Sites explicitly owned by the Owner
    onAddLottery: (List<Campsite>,String,Date,Date,Date,Date,Int,MutableList<Entrant>,Int) -> Unit,
    onUpdateLottery: (Lottery) -> Unit
){
    var backB by remember {mutableStateOf(false)} // used in button presses for changing screens
    var creationScreen by remember {mutableStateOf(false)}
    var updateScreen by remember {mutableStateOf(false)}
    var campInfo: MutableList<MutableMap<String,MutableList<Date>>> = mutableListOf<MutableMap<String,MutableList<Date>>>()
    var selectedSite: Campsite = Campsite("","","","","","","","")

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
                            campInfo.add(
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
                    itemsIndexed(campInfo) { index, siteInfo ->
                        CampsiteListing( // call composable function that builds each lottery display
                            siteInfo = siteInfo,
                            onAddSite = {selectedSite = it},
                            onRemoveSite = {selectedSite = Campsite("","","","","","","","")},
                            selectedSite = selectedSite,
                            sites = campsites
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
                                updateScreen = true
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

                        if (updateScreen) {
                            for (lottery in lotteries) {
                                if(selectedSite in lottery.campsites) {
                                    LotteryUpdateScreen1(lotteries,campsites,lottery,{lottery -> Unit})
                                }
                            }
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
                            creationScreen = true
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
                    if (creationScreen){
                        LotteryCreationScreen1(lotteries, campsites, {campsites, name, regS, regE, bookS, bookE, cap, entrants, winners-> Unit})
                    }
                }
            }
        }
    }
}


/**
 * Reusable Composable object that creates a display of a campsite name and the registration dates of it's
 * associated lottery.
 *
 * Only one checkbox can be selected at one time to ensure the correct lottery is passed to the update function in
 * the LotteryListCampsiteSelectScreen() function. Otherwise, just holds text.
 *
 * @property siteInfo a map with the campsite name as the key, and a mutable list containing the registration
 * dates as its value.
 * @property onAddSite a lambda function that adds the selected campsite to the selectedSite variable by passing
 * it back to LotteryListCampsiteSelectScreen()
 * @property onRemoveSite a lambda function that removes the selected campsite from the selectedSite variable by passing
 * it back to LotteryListCampsiteSelectScreen()
 * @property selectedSite a Campsite object to be used in determining the lottery to be passed to
 * LotteryUpdateScreen1()
 * @property sites a mutable list of Campsite objects
 */
@Composable
fun CampsiteListing (
    siteInfo: MutableMap<String,MutableList<Date>>,
    onAddSite: (Campsite) -> Unit,
    onRemoveSite: (Campsite) -> Unit,
    selectedSite: Campsite,
    sites: List<Campsite>
) {
    var isChecked by remember { mutableStateOf(false)}
    val key = siteInfo.keys.toList()[0]
    val dates = siteInfo[key]
    Row (
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                if(selectedSite.id == "") {
                    isChecked = it
                    for (site in sites) {
                        if(key == site.name) {
                            onAddSite(site)
                        }
                    }
                } else {
                    onRemoveSite(selectedSite)
                }
                              },
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