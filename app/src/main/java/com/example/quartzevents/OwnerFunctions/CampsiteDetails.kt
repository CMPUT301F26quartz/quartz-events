package com.example.quartzevents.OwnerFunctions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quartzevents.Campsite
import java.nio.file.WatchEvent

private val QuartzGreen = Color(0xFF254D3C)
private val QuartzWhite = Color(0xFFFFFFFF)
private val QuartzCream = Color(0xFFF7F5ED)
private val QuartzFieldGray = Color(0xFFD9D9D9)


@Composable
fun CampsiteDetails(
    campsiteId: String,
    onBack: () -> Unit
) {
    val repository = remember { CampsiteRepository() }
    var campsite by remember { mutableStateOf<Campsite?>(null) }

    LaunchedEffect(campsiteId) {
        repository.getCampsiteById(campsiteId) { result, error ->
            if (result != null) {
                campsite = result
            } else {
                println("Error: $error")
            }
        }
    }

    if (campsite == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF254D3C))
        }
        return
    }

    val data = campsite!!

    Column(modifier = Modifier.fillMaxSize()
        .background(QuartzCream)) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(QuartzGreen)
            .padding(top = 40.dp, bottom = 16.dp, start = 16.dp)
            ){
            Row(verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = "←",
                    color = QuartzWhite,
                    fontSize = 32.sp,
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(end = 16.dp)
                )

                Text("Campsite Details", color = QuartzWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())){

            Text(text = data.name,
                color = QuartzGreen,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(QuartzFieldGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "image holder/space",
                        color = QuartzGreen,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(100.dp))
                    Spacer(modifier = Modifier.width(16.dp))


                }

            }

            Spacer(modifier = Modifier.height(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
            }

                Text(
                    text = "Description/Amenities:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuartzGreen
                )

                Text(
                    text = data.description,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = "Location: ",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = QuartzGreen
                )
                Text(data.location, fontSize = 15.sp)


                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Maximum occupancy: ${data.maxOccupancy} guests",
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tags: ${data.tags}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                Spacer(modifier = Modifier.height(24.dp))

                androidx.compose.material3.Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = QuartzGreen
                    )
                ) {
                    Text("Edit Campsite", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }








