package com.example.quartzevents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HouseSiding
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
@Composable
fun AdminDashboardScreen(){
    var openCampsites by remember { mutableStateOf(false) }
    var openProfiles by remember { mutableStateOf(false) }
    var openImages by remember { mutableStateOf(false) }
    var openActivityLog by remember { mutableStateOf(false) }

    
    if(openCampsites){AdminBrowseCampsitesScreen({openCampsites=false})}
    else if(openProfiles){AdminBrowseProfilesScreen({openProfiles=false})}

    else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F6ED))
                .verticalScroll(rememberScrollState())
                .safeDrawingPadding()
                .imePadding(),
//        horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Title Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFF254D3C)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Admin Dashboard",
                    color = Color(0xFFFFFFFF),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            //Title 2
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Platform Management",
                color = Color(0xFF244D3D),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
            )

            //Box Buttons
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                //***** Manage Campsites Box *****
                Box(
                    modifier = Modifier
                        .size(350.dp, 80.dp)
                        .background(color = Color(0xFFD9D9D9), shape = RoundedCornerShape(10.dp))
                        .clickable { openCampsites = true }
                ) {
                    //Campsites Icon
                    Image(
                        Icons.Default.HouseSiding,
                        "Campsites Icon",
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 20.dp)
                            .size(50.dp)
                    )
                    //Texts
                    Column {
                        Text(
                            text = "Manage Campsites",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(start = 60.dp, top = 17.dp)
                        )
                        Text(
                            text = "Browse and Remove Campsites",
                            fontSize = 15.sp,
                            modifier = Modifier.padding(start = 60.dp, top = 5.dp)
                        )
                    }
                }//End Manage Campsites

                //***** Manage Profiles Box *****
                Spacer(modifier = Modifier.height(15.dp))
                Box(
                    modifier = Modifier
                        .size(350.dp, 80.dp)
                        .background(color = Color(0xFFD9D9D9), shape = RoundedCornerShape(10.dp))
                        .clickable { openProfiles = true }
                ) {
                    //Profile Icons
                    Image(
                        Icons.Default.Person,
                        "Profiles Icon",
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 20.dp)
                            .size(50.dp)
                    )
                    //Texts
                    Column {
                        Text(
                            text = "Manage Profiles",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(start = 60.dp, top = 17.dp)
                        )
                        Text(
                            text = "Browse and Remove Accounts",
                            fontSize = 15.sp,
                            modifier = Modifier.padding(start = 60.dp, top = 5.dp)
                        )
                    }
                }//End Manage Profiles

                //***** Manage Images Box *****
                Spacer(modifier = Modifier.height(15.dp))
                Box(
                    modifier = Modifier
                        .size(350.dp, 80.dp)
                        .background(color = Color(0xFFD9D9D9), shape = RoundedCornerShape(10.dp))
                        .clickable { println("Images Box Clicked") }
                ) {
                    //Image Icon
                    Image(
                        Icons.Default.Image,
                        "Image Icon",
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 20.dp)
                            .size(50.dp)
                    )
                    //Texts
                    Column {
                        Text(
                            text = "Manage Images",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(start = 60.dp, top = 17.dp)
                        )
                        Text(
                            text = "Review Uploaded Images",
                            fontSize = 15.sp,
                            modifier = Modifier.padding(start = 60.dp, top = 5.dp)
                        )
                    }
                }//End Manage Images

                //***** Activity Logs Box *****
                Spacer(modifier = Modifier.height(15.dp))
                Box(
                    modifier = Modifier
                        .size(350.dp, 80.dp)
                        .background(color = Color(0xFFD9D9D9), shape = RoundedCornerShape(10.dp))
                        .clickable { println("Activity Logs Box Clicked") }
                ) {
                    //List Icon
                    Image(
                        Icons.Default.List,
                        "List Icon",
                        modifier = Modifier
                            .padding(horizontal = 5.dp, vertical = 20.dp)
                            .size(50.dp)
                    )
                    //Texts
                    Column {
                        Text(
                            text = "Activity Logs",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(start = 60.dp, top = 17.dp)
                        )
                        Text(
                            text = "Notifications and Messages",
                            fontSize = 15.sp,
                            modifier = Modifier.padding(start = 60.dp, top = 5.dp)
                        )
                    }
                }//End Activity Logs

            }//End Boxes

        }
    }

}