/**
 * First of two Screens used in the creation/registration of new lotteries
 *
 * This File deals with the selection of which of the owner's campsites are to be included in the
 * lottery.
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
import com.example.quartzevents.R
import com.example.quartzevents.Repositories.CampsiteRepoMain
import com.example.quartzevents.model.UserProfile

/**
 * This function is the main controlling function for this screen
 *
 * The function contains the buttons to go back to the previous screen, and onto the next,
 * as well as the lazy column containing the campsite listings to be selected for the lottery.
 * At least one campsite must be selected before the "create new lottery" button will be functional
 * and take the user to the next screen.
 *
 * @property loteries a mutable list of Lottery objects to be passed to other functions
 * @property campsites a mutable list of Campsite objects to be used in generating the
 * campsite listings in the lazy column
 * @property onAddLottery a lambda function to be passed to other functions
 */
@Composable
fun LotteryCreationScreen1(
    profile: UserProfile,
    onSelectSites: (String) -> Unit,
    onBack: () -> Unit
){
    val campRepo = remember { CampsiteRepoMain() }
    val ownerSites = campRepo.getOwnerCampsites(profile.uid)
    var selectedSites by remember {mutableStateOf("")}

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
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 25.dp, start = 25.dp),
            ) {
                Text(
                    "List Campsites Into Lottery",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                LazyColumn( // List of available Lottery displays
                    modifier = Modifier.fillMaxSize().weight(1f)
                ) {
                    itemsIndexed(ownerSites) { index, site ->
                        SiteNameListing( // call composable function that builds each lottery display
                            selectedSites,
                            site = site,
                            onAddSite = { sites -> selectedSites = sites },
                            onRemoveSite = { sites -> selectedSites = sites }
                        )
                        if (index < ownerSites.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }

                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
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
                            "Create new Lottery",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
    BackHandler(onBack = onBack)
}

/**
 * Composable function that creates the campsite listing UI elements
 *
 * Consists of a checkbox that adds the site to a list of campsites when checked,
 * and a box containing the campsite's name.
 *
 * @property site a Campsite object to be displayed
 * @property onAddSite a lambda function used to add the site to a list when the checkbox is checked
 * @property onRemoveSite a lambda function used to remove the site from the list when the checkbox is unchecked
 */
@Composable
fun SiteNameListing (
    siteIds: String,
    site: Campsite,
    onAddSite: (String) -> Unit,
    onRemoveSite: (String) -> Unit
) {
    val siteId = site.id
    var isChecked by remember {mutableStateOf(false)}
    Row (
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
                if(isChecked) {
                    val sites = siteIds + " " + siteId
                    onAddSite(sites)
                } else if(!isChecked){
                    var sites = ""
                    val ids = siteIds.split(" ")
                    sites += ids[0]
                    for (id in (1..ids.size)) {
                        if (ids[id] != siteId) {
                           sites += " " + ids[id]
                        }
                    }
                    onRemoveSite(sites)
                }
                              },
        )
        Box (
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