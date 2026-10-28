package com.example.quartzevents

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quartzevents.ui.theme.QuartzEventsTheme

// Temporary screen colors.
// TODO: Move to the shared theme when it is ready.
private val darkGreen = Color(0xFF234F40)
private val backgroundCream = Color(0xFFF8F6ED)
private val cardGray = Color(0xFFD9D9D9)

@Composable
fun PoliciesScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundCream)
            .safeDrawingPadding()
    ) {
        // header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(darkGreen)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onBack,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("←", fontSize = 30.sp)
            }

            Text(
                text = "Policies & Guidelines",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        //scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Quartz Policies & Guidelines",
                color = darkGreen,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            PolicyCard(
                title = "1. Lottery Participation",
                description = "Entrants must follow the eligibility requirements and registration deadlines for each campsite lottery."
            )

            PolicyCard(
                title = "2. Reservation & Cancellations",
                description = "Entrants must confirm reservations within the required period and follow the campsite's cancellation policies."
            )

            PolicyCard(
                title = "3. Respectful Conduct",
                description = "Users are expected to behave respectfully and comply with campsite rules and community guidelines."
            )

            PolicyCard(
                title = "4. Privacy & Personal Information",
                description = "Personal information is handled according to the app’s privacy policy and used to support account and reservation features."
            )
            
            PolicyCard(
                title = "5. Violations & Account Actions",
                description = "Violations of app guidelines may result in administrative action, subject to the app’s published policies."
            )
        }
    }
}

@Composable
private fun PolicyCard(
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardGray
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                color = darkGreen,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                color = darkGreen
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PoliciesScreenPreview() {
    QuartzEventsTheme {
        PoliciesScreen(onBack = {})
    }
}