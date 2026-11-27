/**
 * The second of Two Screens used in the creation/reistration of new Lotteries
 *
 * Collects the rest of the necessary information to create the lottery
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quartzevents.Classes.Lottery
import com.example.quartzevents.R
import com.example.quartzevents.Repositories.LotteryRepository
import com.example.quartzevents.model.UserProfile


@Composable
fun LotteryCreationScreen2(
    profile: UserProfile,
    selectedSites: String?,
    onCreation: () -> Unit,
    onBack: () -> Unit
) {
    val lotRepo = remember { LotteryRepository() }
    val selectedSitesList = selectedSites?.split(" ") // list of campsite ids
    var regStartInput by remember { mutableStateOf("") }
    var regEndInput by remember { mutableStateOf("") }
    var bookStartInput by remember { mutableStateOf("") }
    var bookEndInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var capacityInput by remember { mutableStateOf("") }
    var entrantsInput: MutableList<String> = mutableListOf()
    var winnersInput: MutableList<String> = mutableListOf()
    var winNumInput by remember {mutableStateOf("")}
    val dateFormat =
        Regex("""^(\d{4})-(0[1-9]|1[0-2]|[1-9])-([1-9]|0[1-9]|[1-2]\d|3[0-1])$""") // for checking if entered date is in the correct format: from https://regex101.com/library/oE3yO7 and https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.text/-regex/
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
                    .padding(top = 25.dp, start = 25.dp)
                    .verticalScroll(
                        rememberScrollState()
                    )
            ) {
                Text(
                    "Lottery Details",
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
                    modifier = Modifier.size(320.dp, 65.dp)
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
                        .padding(start = 12.dp, top = 8.dp),
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
                        .padding(start = 10.dp, top = 5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(
                        value = regStartInput,
                        onValueChange = { regStartInput = it },
                        singleLine = true,
                        modifier = Modifier.size(150.dp, 65.dp)
                            .background(color = Color.LightGray),
                        label = { Text("YYYY-MM-DD", color = Color.Gray) }
                    )
                    OutlinedTextField(
                        value = regEndInput,
                        onValueChange = { regEndInput = it },
                        singleLine = true,
                        modifier = Modifier.size(150.dp, 65.dp)
                            .background(color = Color.LightGray),
                        label = { Text("YYYY-MM-DD", color = Color.Gray) }
                    )
                }

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Text(
                    buildAnnotatedString {
                        append("Booking Period")
                        withStyle(superscript) {
                            append("*")
                        }
                    },
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp,
                        0.dp)
                )

                Text(
                    "Specify the dates when the campsites will be available for booking.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 25.dp, top = 5.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, top = 8.dp),
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
                        .padding(start = 10.dp, top = 5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedTextField(
                        value = bookStartInput,
                        onValueChange = { bookStartInput = it },
                        singleLine = true,
                        modifier = Modifier.size(150.dp, 65.dp)
                            .background(color = Color.LightGray),
                        label = { Text("YYYY-MM-DD", color = Color.Gray) }
                    )
                    OutlinedTextField(
                        value = bookEndInput,
                        onValueChange = { bookEndInput = it },
                        singleLine = true,
                        modifier = Modifier.size(150.dp, 65.dp)
                            .background(color = Color.LightGray),
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
                    value = winNumInput,
                    onValueChange = { newText ->
                        if(newText.all {it.isDigit()})
                            winNumInput = newText
                                    },
                    singleLine = true,
                    modifier = Modifier.size(320.dp, 65.dp)
                        .background(color = Color.LightGray),
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
                    value = capacityInput,
                    onValueChange = { newText ->
                        if(newText.all {it.isDigit()})
                            capacityInput = newText
                    },
                    singleLine = true,
                    modifier = Modifier.size(320.dp, 65.dp)
                        .background(color = Color.LightGray),
                    label = { Text("Please Enter the Maximum Entrants into the Lottery, (or zero for unlimited)", color = Color.Gray) }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding( top = 15.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            val winNum = winNumInput.toIntOrNull() ?: 1
                            val capacity = capacityInput.toIntOrNull() ?: 10
                            if (regStartInput != "" &&
                                regEndInput != "" &&
                                bookStartInput != "" &&
                                bookEndInput != "" &&
                                nameInput != "" &&
                                selectedSitesList!!.isNotEmpty() &&
                                winNum != 0 &&
                                dateFormat.matches(regStartInput) &&
                                dateFormat.matches(regEndInput) &&
                                dateFormat.matches(bookStartInput) &&
                                dateFormat.matches(bookEndInput)
                            ) {
                                lotRepo.addLottery(
                                    Lottery(
                                        selectedSitesList as MutableList<String>,
                                        nameInput,
                                        regStartInput,
                                        regEndInput,
                                        bookStartInput,
                                        bookEndInput,
                                        capacity,
                                        "",
                                        profile.uid,
                                        entrantsInput,
                                        winNum,
                                        winnersInput
                                    )
                                )
                                onCreation()


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
    BackHandler (onBack = onBack) // Handles navigation to previous screen
}