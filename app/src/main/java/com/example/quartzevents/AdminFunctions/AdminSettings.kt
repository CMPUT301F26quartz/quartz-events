package com.example.quartzevents.AdminFunctions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AdminSettingsScreen(back: () -> Unit){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6ED))
            .safeDrawingPadding()
            .imePadding(),
    ) {
        //Banner Title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFF254D3C)),
            contentAlignment = Alignment.Center
        ) {
            Row() {
                //back button, leads back to AdminDashboard
                TextButton(onClick = back) {
                    Text(
                        text = "<",
                        color = Color(0xFFFFFFFF),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                }
                Text(
                    text = "Settings",
                    color = Color(0xFFFFFFFF),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 5.dp)
                )
            }
        }//end Banner Box



    }
}