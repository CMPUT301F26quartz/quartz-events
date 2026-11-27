package com.example.quartzevents.LotteryFunctions

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.quartzevents.Classes.Lottery

@Composable
internal fun LotteryStatusScreen(lottery: Lottery, userId: String, registrationOpen: Boolean) {
    val status = when {
        userId in lottery.winners -> "You won this lottery."
        userId in lottery.entrants && lottery.drawnAt != null -> "You were not selected."
        userId in lottery.entrants -> "You are on the waitlist."
        registrationOpen -> "Registration is open."
        else -> "Registration is closed."
    }
    Text(status)
}
