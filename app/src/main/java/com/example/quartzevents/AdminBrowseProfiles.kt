package com.example.quartzevents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminBrowseProfilesScreen(back: () -> Unit){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6ED))
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding()
            .imePadding(),
    ) {
        //Banner Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFF254D3C)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row() {
                //back button, leads back to AdminDashboard
                TextButton(onClick = back) {
                    Text(
                        text = "<",
                        color = Color(0xFFFFFFFF),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp),
                    )
                }
                Text(
                    text = "Manage Profiles",
                    color = Color(0xFFFFFFFF),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 10.dp)
                )
            }
        }//end Banner Box

        //Title 2
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Registered Profiles",
            color = Color(0xFF244D3D),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))


        //for preview purposes
        val users: List<User> = listOf(Entrant("email1", "Name 1", "pass1"), Owner("email2", "Name 2", "pass2"))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for(user in users){
                Box(
                    modifier = Modifier
                        .size(350.dp, 105.dp)
                        .background(color = Color(0xFFD9D9D9), shape = RoundedCornerShape(10.dp)),
                ) {
                    Column {
                        //user name
                        Text(
                            text = user.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 15.dp, top = 15.dp)
                        )
                        //user role
                        Text(
                            text = "Role: ${user.userType()}",
                            fontSize = 15.sp,
                            modifier = Modifier.padding(start = 15.dp, top = 5.dp)
                        )
                    }

                    //TODO: add violation status

                    //view details
                    Text(
                        //TODO: make "View Details" work
                        //TODO: lead to opening the details page
                        text = "View Details >>",
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 15.dp, bottom = 5.dp),
                        color = Color(0xFF324CA8)
                    )
                    //remove button
                    Button(
                        //TODO: Make Remove button work
                        onClick = { println("Remove button clicked") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .padding(end = 12.dp, bottom = 5.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Text(text = "Remove")
                    }
                }//end box
                Spacer(modifier = Modifier.height(10.dp))
            }//end loop
        }
    }
}