package com.example.quartzevents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quartzevents.model.UserProfile

/**
 * Shows the signed-in entrant's home page with campsite search and logout actions.
 *
 * @param profile The signed-in user's profile.
 * @param onSearch Opens the campsite search page.
 * @param onLogout Signs the user out.
 */
@Composable
fun EntrantHomeScreen(profile: UserProfile, onSearch: () -> Unit, onLogout: () -> Unit) {
    val darkGreen = Color(0xFF244D3D)
    val backgroundCream = Color(0xFFF7F5ED)
    val cardGray = Color(0xFFD9D9D9)

    Column(
        Modifier.fillMaxSize().background(backgroundCream)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
    ) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Column(
                Modifier.fillMaxWidth().background(darkGreen)
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                    .padding(horizontal = 28.dp, vertical = 28.dp)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Quartz", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Role: Entrant", color = Color.White, fontSize = 12.sp)
                }
                Spacer(Modifier.height(28.dp))
                Text("Welcome back!", color = Color.White, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                Text(profile.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text("Find Your Next Campsite", color = darkGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onSearch,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkGreen, contentColor = Color.White)
                ) {
                    Text("Explore Campsites →", fontWeight = FontWeight.Bold)
                }
            }
        }

        TextButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp).heightIn(min = 48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.textButtonColors(containerColor = cardGray, contentColor = darkGreen)
        ) {
            Text("Log Out", fontWeight = FontWeight.Bold)
        }
    }
}
