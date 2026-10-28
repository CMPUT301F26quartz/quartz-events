package com.example.quartzevents

import android.R
import android.R.attr.text
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

@Composable
fun AdminBrowseCampsitesScreen(onBack: () -> Unit){
    BackHandler(onBack = onBack)
// TODO: make sure everything looks good and works as needed
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6ED))
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFF254D3C)),
//            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Manage Campsites",
                color = Color(0xFFFFFFFF),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Registered Campsites",
                color = Color(0xFF244D3D),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            //gets an updated list of campsites from Firebase and lists them one by one
            CampsiteRepository.update()
            val campsites by CampsiteRepository.allCampsites.collectAsState()
            LazyColumn() {
                items(campsites) {campsite ->
                    Box(
                        modifier = Modifier
                            .size(100.dp, 30.dp)
                            .background(color = Color(0xFFD9D9D9), shape = RoundedCornerShape(10.dp))
                    )
                    Text(
                        text = campsite.name,
                    )
                    Text(
                        text = campsite.location
                    )
                    Row{
                        Text(
                            //TODO: make "View Details" work
                            //TODO: lead to opening the details page
                            text = "View Details >>",
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF324CA8)
                        )
                        Button(
                            onClick = {println("Delete button clicked")},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            )
                        ){
                            Text(text = "Delete")
                        }
                    }

                }
            }//end LazyColumn

        }
    }
}