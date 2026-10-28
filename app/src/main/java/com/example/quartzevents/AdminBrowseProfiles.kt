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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminBrowseProfilesScreen(){
//    onBack: () -> Unit
//    BackHandler(onBack = onBack)
    //TODO: make back button
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6ED))
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding()
            .imePadding(),
//        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Color(0xFF254D3C)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Manage Profiles",
                color = Color(0xFFFFFFFF),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

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
                        .size(350.dp, 110.dp)
                        .background(color = Color(0xFFD9D9D9), shape = RoundedCornerShape(10.dp)),
//                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Text(
                            text = user.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                        Text(
                            text = "Role: ${user.userType()}",
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )

                        //TODO: add policy violations

                        Row {
                            Text(
                                //TODO: make "View Details" work
                                //TODO: lead to opening the details page
                                text = "View Profile >>",
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 15.dp),
                                color = Color(0xFF324CA8)
                            )
                            Button(
                                onClick = { println("Remove button clicked") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.padding(start = 115.dp),
                            ) {
                                Text(text = "Remove")
                            }
                        }

                    }
                }//end box
                Spacer(modifier = Modifier.height(10.dp))
            }//end loop
        }
    }
}

@Preview(name = "Admin Profiles Preview", showBackground = true)
@Composable
fun AdminProfilesPreview() {
    AdminBrowseProfilesScreen()
}