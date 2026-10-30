package com.example.quartzevents.OwnerFunctions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quartzevents.model.UserProfile
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.quartzevents.OwnerFunctions.CampsiteRepository
import com.example.quartzevents.OwnerFunctions.Campsite




private val QuartzGreen = Color(0xFF254D3C)
private val QuartzWhite = Color(0xFFFFFFFF)
private val QuartzCream = Color(0xFFF7F5ED)

@Composable

fun OwnerHomeScreen (
    profile: UserProfile,
    onAddCampsiteClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogout: () -> Unit) {

    var isCreatingCampsite by rememberSaveable { mutableStateOf(false) }
    var isViewingSettings by rememberSaveable { mutableStateOf(false) }

    val repository = remember { CampsiteRepository() }
    var campsites by remember { mutableStateOf<List<Campsite>>(emptyList()) }



    LaunchedEffect(Unit) {
        repository.getOwnerCampsites(profile.uid) { result, _ ->
            if (result != null) {
                campsites = result
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QuartzCream)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(QuartzGreen),
        ) {
            Text(

                modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp),
                text = "My campsites",
                fontSize = 30.sp,
                color = QuartzWhite,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold



            )
            Button(onClick = onLogout,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 30.dp, end = 10.dp)

            ) {
                Text("Logout")
            }


        }



        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text(
                fontSize = 22.sp,
                text = "Your registered Campsites",
                color = QuartzGreen,
                fontWeight = FontWeight.Bold


            )
            Spacer(modifier = Modifier
                .height(24.dp)
                .padding(end = 30.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for(campsite in campsites){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(110.dp)
                            .background(color = Color(0xFFD9D9D9), shape = RoundedCornerShape(10.dp)),
                    ) {
                        Column {
                            Text(
                                text = campsite.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                            Text(
                                text = campsite.location,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(horizontal = 14.dp)
                            )
                            Text(
                                text = "Max guests: ${campsite.maxOccupancy}",
                                fontSize = 15.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                            )

                            Row {
                                Text(
                                    text = "View Details →",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    color = Color(0xFF254D3C)
                                )
                            }
                        }
                    }
                }
            }

            Button(onClick = onAddCampsiteClick,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 30.dp, end = 10.dp))
            {Text("add campsite")}


        }
    }
}}