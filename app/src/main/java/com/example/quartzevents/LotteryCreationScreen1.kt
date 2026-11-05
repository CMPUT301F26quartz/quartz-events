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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LotteryCreationScreen1(
    lotteries: List<Lottery>, // lotteries list - connects to db
    campsites: MutableList<Campsite> // Sites explicitly owned by the Owner
){
    var back_b by remember {mutableStateOf(false)} // used in button presses for changing screens
    var next_screen by remember {mutableStateOf(false)}
    var selected_sites = remember {mutableStateListOf<String>()}
    var regStartInput by remember {mutableStateOf("")}
    var regEndInput by remember {mutableStateOf("")}
    val date_Format = Regex("""^(19|20)\d{2}-(0[1-9]|1[0,1,2])-(0[1-9]|[12][0-9]|3[01])$""") // for checking if entered date is in the correct format

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ){
        Row (
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp,top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        "From:",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        "To:",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp,top = 5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(
                        value = regStartInput,
                        onValueChange = {regStartInput = it},
                        singleLine = true,
                        modifier = Modifier.size(100.dp, 30.dp).background(color = Color.LightGray, shape = RoundedCornerShape(10.dp)),
                        label = {Text("YYYY-MM-DD",color = Color.Gray)}
                    )
                    OutlinedTextField(
                            value = regEndInput,
                        onValueChange = {regEndInput = it},
                        singleLine = true,
                        modifier = Modifier.size(100.dp, 30.dp).background(color = Color.LightGray, shape = RoundedCornerShape(10.dp)),
                        label = {Text("YYYY-MM-DD",color = Color.Gray)}
                    )
                }

                Spacer(modifier = Modifier.padding(top = 20.dp))

                LazyColumn( // List of available Lottery displays
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(campsites) { index, site ->
                        SiteNameListing( // call composable function that builds each lottery display
                            site = site,
                            onAddSite = {selected_sites.add(it)},
                            onRemoveSite = {selected_sites.remove(it)}
                        )
                        if (index < campsites.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                }

                    Button(
                        onClick = {
                            if (regStartInput != "" && regEndInput != "" && campsites.isNotEmpty() && date_Format.matches(regStartInput) && date_Format.matches(regEndInput)) {
                                next_screen = true
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
                    if (next_screen){
                        // next screen here
                    }
                }
            }
        }
    }


@Composable
fun SiteNameListing (
    site: Campsite,
    onAddSite: (String) -> Unit,
    onRemoveSite: (String) -> Unit
) {
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
                    onAddSite(site.name)
                } else if(!isChecked){
                    onRemoveSite(site.name)
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