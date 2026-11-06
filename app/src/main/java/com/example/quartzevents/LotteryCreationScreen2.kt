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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LotteryCreationScreen2(
    lotteries: List<Lottery>, // lotteries list - connects to db
    campsites: MutableList<Campsite>, // Sites explicitly owned by the Owner
    selectedSites: MutableList<Campsite>,
    onAddLottery: (MutableList<Campsite>,String,Date,Date,Date,Date,Int,String,MutableList<Entrant>,Int) -> Unit
) {
    var backB by remember { mutableStateOf(false) } // used in button presses for changing screens
    var regStartInput by remember { mutableStateOf("") }
    var regEndInput by remember { mutableStateOf("") }
    var bookStartInput by remember { mutableStateOf("") }
    var bookEndInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var capacityInput by remember { mutableIntStateOf(0) }
    var entrantsInput: MutableList<Entrant> = mutableListOf()
    var winnersInput by remember {mutableIntStateOf(0)}
    val dateFormat =
        Regex("""^(19|20)\d{2}-(0[1-9]|1[0,1,2])-(0[1-9]|[12][0-9]|3[01])$""") // for checking if entered date is in the correct format
    val superscript = SpanStyle(
        baselineShift = BaselineShift.Superscript,
        fontSize = 16.sp,
        color = Color.Red
    )

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
                onClick = {
                    backB = true
                }
            ) {
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
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 25.dp, start = 25.dp)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                Text(
                    "List Campsites Into Lottery",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    buildAnnotatedString {
                        append("Fields marked with ")
                        withStyle(superscript) {
                            append("*")
                        }
                        append(" are mandatory required fields.")
                    },
                    fontSize = 15.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp,10.dp)
                )

                Text(
                    buildAnnotatedString {
                        append("Lottery Name")
                        withStyle(superscript) {
                            append("*")
                        }
                    },
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp,10.dp)
                )

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    singleLine = true,
                    modifier = Modifier.size(320.dp, 30.dp)
                        .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp)),
                    label = { Text("Please Enter the Name of the Lottery", color = Color.Gray) }
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Text(
                    buildAnnotatedString {
                        append("Registration Period")
                        withStyle(superscript) {
                            append("*")
                        }
                    },
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp,10.dp)
                )

                Text(
                    "Define when entrants can join the lottery.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 25.dp, top = 5.dp)
                )


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, top = 8.dp),
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
                        .padding(start = 30.dp, top = 5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(
                        value = regStartInput,
                        onValueChange = { regStartInput = it },
                        singleLine = true,
                        modifier = Modifier.size(100.dp, 30.dp)
                            .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp)),
                        label = { Text("YYYY-MM-DD", color = Color.Gray) }
                    )
                    OutlinedTextField(
                        value = regEndInput,
                        onValueChange = { regEndInput = it },
                        singleLine = true,
                        modifier = Modifier.size(100.dp, 30.dp)
                            .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp)),
                        label = { Text("YYYY-MM-DD", color = Color.Gray) }
                    )
                }

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Text(
                    buildAnnotatedString {
                        append("Registration Period")
                        withStyle(superscript) {
                            append("*")
                        }
                    },
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp,10.dp)
                )

                Text(
                    "Define when entrants can join the lottery.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 25.dp, top = 5.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, top = 8.dp),
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
                        .padding(start = 30.dp, top = 5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(
                        value = bookStartInput,
                        onValueChange = { bookStartInput = it },
                        singleLine = true,
                        modifier = Modifier.size(100.dp, 30.dp)
                            .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp)),
                        label = { Text("YYYY-MM-DD", color = Color.Gray) }
                    )
                    OutlinedTextField(
                        value = bookEndInput,
                        onValueChange = { bookEndInput = it },
                        singleLine = true,
                        modifier = Modifier.size(100.dp, 30.dp)
                            .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp)),
                        label = { Text("YYYY-MM-DD", color = Color.Gray) }
                    )
                }

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Text(
                    buildAnnotatedString {
                        append("Number of Winners")
                        withStyle(superscript) {
                            append("*")
                        }
                    },
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp,10.dp)
                )

                OutlinedTextField(
                    value = winnersInput.toString(),
                    onValueChange = { winnersInput = it.toInt() },
                    singleLine = true,
                    modifier = Modifier.size(320.dp, 30.dp)
                        .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp)),
                    label = { Text("Please Enter the Number of Winners of the Lottery", color = Color.Gray) }
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Text(
                    "Maximum Number of Entrants",
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp,10.dp)
                )

                OutlinedTextField(
                    value = capacityInput.toString(),
                    onValueChange = { capacityInput = it.toInt() },
                    singleLine = true,
                    modifier = Modifier.size(320.dp, 30.dp)
                        .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp)),
                    label = { Text("Please Enter the Maximum Entrants into the Lottery, (or zero for unlimited)", color = Color.Gray) }
                )

                Button(
                    onClick = {
                        if (regStartInput != "" &&
                            regEndInput != "" &&
                            bookStartInput != "" &&
                            bookEndInput != "" &&
                            nameInput != "" &&
                            selectedSites.isNotEmpty() &&
                            entrantsInput.isNotEmpty() &&
                            winnersInput != 0 &&
                            dateFormat.matches(regStartInput) &&
                            dateFormat.matches(regEndInput) &&
                            dateFormat.matches(bookStartInput) &&
                            dateFormat.matches(bookEndInput)
                        ) {
                            onAddLottery(selectedSites,
                                nameInput,
                                Date(regStartInput),
                                Date(regEndInput),
                                Date(bookStartInput),
                                Date(bookStartInput),
                                capacityInput,
                                "id",
                                entrantsInput,
                                winnersInput)

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