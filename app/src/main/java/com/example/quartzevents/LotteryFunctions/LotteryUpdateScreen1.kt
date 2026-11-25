package com.example.quartzevents.LotteryFunctions

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
import com.example.quartzevents.Campsite
import com.example.quartzevents.Classes.Lottery
import com.example.quartzevents.R
import com.example.quartzevents.Repositories.CampsiteRepoMain
import com.example.quartzevents.Repositories.LotteryRepository
import com.example.quartzevents.model.UserProfile


@Composable
fun LotteryUpdateScreen1(
    profile: UserProfile,
    lotteryId: String?,
    onSelectSites: (String) -> Unit,
    onBack: () -> Unit
) {
    val campRepo = CampsiteRepoMain()
    var campsites = campRepo.getAllCamsites()
    val ownerSites = campRepo.getOwnerCampsites(profile.uid)
    val lotRepo = LotteryRepository()
    val lottery = lotRepo.getLottery(lotteryId!!)
    var nextScreen by remember { mutableStateOf(false) }
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
                "Lottery Update",
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
                    .padding(top = 25.dp, start = 25.dp),
            ) {
                Text(
                    "Update Campsites in Lottery",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Text(
                    "Your Campsites:",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                LazyColumn( // List of available Lottery displays
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(ownerSites) { index, site ->
                        SiteUpdateNameListing( // call composable function that builds each lottery display
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

                var nonOwnerSites = mutableListOf<Campsite>()
                for (site in campsites) {
                    if (site !in ownerSites) {
                        nonOwnerSites.add(site)
                    }
                }


                Spacer(modifier = Modifier.padding(top = 20.dp))

                Text(
                    "Other Campsites:",
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                LazyColumn( // List of available Lottery displays
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(nonOwnerSites) { index, site ->
                        SiteUpdateNameListing( // call composable function that builds each lottery display
                            selectedSites,
                            lottery = lottery,
                            site = site,
                            onAddSite = { selectedSites = it },
                            onRemoveSite = { selectedSites = it }
                        )
                        if (index < nonOwnerSites.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Button(
                    onClick = {
                        if (selectedSites != "") {
                            onSelectSites(selectedSites)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "Continue Lottery Update",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                BackHandler(onBack = onBack)
            }
        }
    }
}


@Composable
fun SiteUpdateNameListing(
    siteIds: String,
    lottery: Lottery?,
    site: Campsite,
    onAddSite: (String) -> Unit,
    onRemoveSite: (String) -> Unit
) {
    val siteId = site.id
    var isChecked by remember { mutableStateOf(false) }
    if (site in lottery!!.campsites) {
        isChecked = true
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, bottom = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                if (isChecked) {
                    val sites = siteIds + " " + siteId
                    onAddSite(sites)
                } else if (!isChecked) {
                    var sites = ""
                    val ids = siteIds.split(" ")
                    sites += ids[0]
                    for (id in (1..ids.size)) {
                        if (ids[id] != siteId) {
                            sites += " " + ids[id]
                        }
                    }
                    onRemoveSite(siteId)
                }
            },
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


