/**
 * Alternate Screen to normal lottery update process
 * only used when the user selects a lottery they
 * did not create. Selected Lottery info is displayed,
 * and they are able to add one or more of their
 * own campsites to the lottery as well via checkboxes
 * and a confirm button.
 */

package com.example.quartzevents.OwnerFunctions

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun LotteryNonOwnerUpdateScreen(
    profile: UserProfile,
    lotteryId: String?,
    onUpdate: () -> Unit,
    onBack: () -> Unit
) {
    val campRepo = remember { CampsiteRepoMain() }
    val ownerSites = campRepo.getOwnerCampsites(profile.uid)
    val lotRepo = remember { LotteryRepository() }
    val lottery = lotRepo.getLottery(lotteryId!!)
    var selectedSites by remember { mutableStateOf("") }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 45.dp)
        ) {
            IconButton( // back button
                modifier = Modifier.padding(start = 15.dp),
                onClick = onBack
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
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
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 25.dp, start = 25.dp)
                    .verticalScroll(rememberScrollState()),
            ) {

                Text(
                    "Name: "+lottery!!.name,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                var campsiteString = ""
                lottery.campsites.forEach { site ->
                    val campsite  = campRepo.getCampsite(site)
                    if (campsiteString == "")
                        campsiteString += campsite?.name
                    else
                        campsiteString += " / " + campsite?.name
                }

                Text(
                    "Campsites: "+campsiteString,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding( start = 10.dp,top = 10.dp),
                ) {
                    Text(
                        "Registration Start: "+lottery.registrationStart,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        "Registration end: "+lottery.registrationEnd,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding( start = 10.dp,top = 10.dp),
                ) {
                    Text(
                        "Booking Start: "+lottery.bookingStart,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        "Booking end: "+lottery.bookingEnd,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding( start = 10.dp,top = 10.dp),
                ) {
                    Text(
                        "Lottery Capacity: "+lottery!!.capacity.toString(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        "Number of Winners: "+lottery.winNum.toString(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Text(
                    "Your Campsites:",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    "You may add any of your campsites to the lottery if you choose.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                LazyColumn( // List of available Lottery displays
                    modifier = Modifier.fillMaxSize().weight(1f)
                ) {
                    itemsIndexed(ownerSites) { index, site ->
                        SiteNonOwnerUpdateNameListing( // call composable function that builds each lottery display
                            selectedSites,
                            lottery = lottery,
                            site = site,
                            onAddSite = { selectedSites = it },
                            onRemoveSite = { selectedSites = it }
                        )
                        if (index < ownerSites.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Button(
                    onClick = {
                        val fields = mutableMapOf<String,Any>()
                        fields["campsites"] = selectedSites
                        lotRepo.updateLottery(fields,lotteryId)
                        onUpdate()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "Add Selected Campsites to Lottery",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                BackHandler(onBack = onBack)
            }
        }
    }
}


@Composable
fun SiteNonOwnerUpdateNameListing(
    siteIds: String,
    lottery: Lottery?,
    site: Campsite,
    onAddSite: (String) -> Unit,
    onRemoveSite: (String) -> Unit
) {
    val siteId = site.id

    // Parse the current selected campsite IDs
    val selectedIds = siteIds.split(" ")
        .filter { it.isNotBlank() }

    // A campsite is checked if it is selected
    val isChecked = siteId in selectedIds

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { checked ->

                val updatedIds = if (checked) {
                    selectedIds + siteId
                } else {
                    selectedIds.filter { it != siteId }
                }

                val updatedSites = updatedIds
                    .distinct()
                    .joinToString(" ")

                if (checked) {
                    onAddSite(updatedSites)
                } else {
                    onRemoveSite(updatedSites)
                }
            }
        )

        Box(
            modifier = Modifier
                .size(300.dp, 80.dp)
                .padding(start = 5.dp)
                .background(color = Color.LightGray, shape = RoundedCornerShape(20.dp))
        ) {
            Text(
                site.name,
                fontSize = 19.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}