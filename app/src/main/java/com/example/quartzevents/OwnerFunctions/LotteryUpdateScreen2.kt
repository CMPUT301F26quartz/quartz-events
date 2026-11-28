package com.example.quartzevents.OwnerFunctions

import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quartzevents.Classes.Date
import com.example.quartzevents.Classes.Lottery
import com.example.quartzevents.R
import com.example.quartzevents.Repositories.LotteryRepository
import com.example.quartzevents.model.UserProfile

@Composable
fun LotteryUpdateScreen2(
    selectedSites: MutableList<String?>,
    lotteryId: String?,
    onUpdate: () -> Unit,
    onBack: () -> Unit
) {
    val lotRepo = remember { LotteryRepository() }
    val lottery = lotRepo.getLottery(lotteryId.toString())
    var regStartInput by remember { mutableStateOf("") }
    var regEndInput by remember { mutableStateOf("") }
    var bookStartInput by remember { mutableStateOf("") }
    var bookEndInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var capacityInput by remember { mutableStateOf("") }
    var winNumInput by remember {mutableStateOf("")}
    var fields = remember {mutableMapOf<String,Any>()}
    val dateFormat =
        Regex("""^(19|20)\d{2}-(0[1-9]|1[0,1,2])-(0[1-9]|[12][0-9]|3[01])$""") // for checking if entered date is in the correct format

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
                    "Fill in the fields you wish to update.",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp,10.dp)
                )

                Text(
                    "Update Lottery Name",
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
                    "Update Registration Period",
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 30.dp,10.dp)
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
                            .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp)),
                        label = { Text("YYYY-MM-DD", color = Color.Gray) }
                    )
                    OutlinedTextField(
                        value = regEndInput,
                        onValueChange = { regEndInput = it },
                        singleLine = true,
                        modifier = Modifier.size(150.dp, 65.dp)
                            .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp)),
                        label = { Text("YYYY-MM-DD", color = Color.Gray) }
                    )
                }

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Text(
                    "Update Booking Period",
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp,10.dp)
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
                            .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp)),
                        label = { Text("YYYY-MM-DD", color = Color.Gray) }
                    )
                    OutlinedTextField(
                        value = bookEndInput,
                        onValueChange = { bookEndInput = it },
                        singleLine = true,
                        modifier = Modifier.size(150.dp, 65.dp)
                            .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp)),
                        label = { Text("YYYY-MM-DD", color = Color.Gray) }
                    )
                }

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Text(
                    "Number of Winners",
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
                    value = capacityInput,
                    onValueChange = { newText ->
                        if(newText.all {it.isDigit()})
                            capacityInput = newText
                    },
                    singleLine = true,
                    modifier = Modifier.size(320.dp, 65.dp)
                        .background(color = Color.LightGray, shape = RoundedCornerShape(10.dp)),
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
                            if (selectedSites.isNotEmpty())
                                fields["campsites"] = selectedSites
                            if (nameInput != "")
                                fields["name"] = nameInput
                            if (regStartInput != "" && dateFormat.matches(regStartInput))
                                fields["registrationStart"] = regStartInput
                            if (regEndInput != "" && dateFormat.matches(regEndInput))
                                fields["registrationEnd"] = regEndInput
                            if (bookStartInput != "" && dateFormat.matches(bookStartInput))
                                fields["bookingStart"] = bookStartInput
                            if (bookEndInput != "" && dateFormat.matches(bookEndInput))
                                fields["bookingEnd"] = bookEndInput
                            if (capacityInput != "")
                                fields["capacity"] = capacityInput.toInt()
                            if (winNumInput != "")
                                fields["winNum"] = winNumInput.toInt()

                            Log.d("LotteryUpdate", "Selected sites: $selectedSites")
                            Log.d("LotteryUpdate", "Fields: $fields")
                            lotRepo.updateLottery(fields, lotteryId!!)
                            onUpdate()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            "Update Lottery",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
    BackHandler(onBack = onBack)
}